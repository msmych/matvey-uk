package uk.matvey.app

import com.auth0.jwt.algorithms.Algorithm
import com.typesafe.config.ConfigFactory
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import org.flywaydb.core.Flyway
import uk.matvey.slon.HikariKit.hikariDataSource
import uk.matvey.slon.repo.Repo
import uk.matvey.utka.jwt.AuthJwt

private val log = KotlinLogging.logger("Matvey")

fun main(args: Array<String>) {
    val profile = Profile.from(args[0])
    val config = ConfigFactory.load("matvey.${profile.name.lowercase()}.conf")
        .withFallback(ConfigFactory.load("matvey.conf"))
    val dbConfig = config.getConfig("db")
    val ds = hikariDataSource(
        jdbcUrl = dbConfig.getString("jdbcUrl"),
        username = dbConfig.getString("username"),
        password = dbConfig.getString("password"),
    )
    val tgConfig = config.getConfig("tg")
    Flyway.configure()
        .dataSource(ds)
        .schemas("public")
        .defaultSchema("public")
        .locations("classpath:db/migration")
        .placeholders(
            mapOf(
                "tgAdminId" to tgConfig.getLong("adminId").toString(),
            )
        )
        .validateMigrationNaming(true)
        .load()
        .migrate()
    val repo = Repo(ds)
    val authJwt = AuthJwt(Algorithm.HMAC256(config.getString("jwtSecret")), "matvey")
    val auth = MatveyAuth(authJwt)
    val serverConfig = config.getConfig("server")
    runBlocking {
        MatveyBot(
            tgConfig = tgConfig,
            serverConfig = serverConfig,
            repo = repo,
            matveyAuth = auth,
            profile = profile
        ).start()
    }
    startServer(
        serverConfig = serverConfig,
        profile = profile,
        auth = auth,
        repo = repo,
    )
}
