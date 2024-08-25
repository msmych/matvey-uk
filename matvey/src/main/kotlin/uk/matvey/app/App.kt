package uk.matvey.app

import com.auth0.jwt.algorithms.Algorithm
import com.typesafe.config.ConfigFactory
import kotlinx.coroutines.runBlocking
import uk.matvey.slon.FlywayKit.flywayMigrate
import uk.matvey.slon.HikariKit.hikariDataSource
import uk.matvey.slon.repo.Repo

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
    flywayMigrate(
        dataSource = ds,
        clean = dbConfig.getBoolean("clean"),
    ) {
        placeholders(
            mapOf(
                "tgAdminId" to tgConfig.getLong("adminId").toString(),
            )
        )
    }
    val repo = Repo(ds)
    val algorithm = Algorithm.HMAC256(config.getString("jwtSecret"))
    val auth = MatveyAuth(algorithm)
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
