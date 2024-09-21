package uk.matvey.app

import com.auth0.jwt.algorithms.Algorithm
import com.typesafe.config.ConfigFactory
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import uk.matvey.app.config.AppConfig
import uk.matvey.falafel.FalafelAuth
import uk.matvey.slon.flyway.FlywayKit.flywayMigrate
import uk.matvey.slon.repo.Repo
import uk.matvey.utka.jwt.AuthJwt

private val log = KotlinLogging.logger("Matvey")

fun main(args: Array<String>) {
    val profile = Profile.from(args[0])
    val config = AppConfig(
        ConfigFactory.load("matvey.${profile.name.lowercase()}.conf")
            .withFallback(ConfigFactory.load("matvey.conf"))
    )
    val dbConfig = config.db()
    val ds = dbConfig.ds()
    val tgConfig = config.tg()
    flywayMigrate(
        dataSource = ds,
        location = "classpath:db/migration",
        clean = dbConfig.clean(),
    ) {
        placeholders(mapOf("tgAdminId" to tgConfig.adminId().toString()))
    }

    val repo = Repo(ds)
    val authJwt = AuthJwt(Algorithm.HMAC256(config.jwtSecret()), "matvey")
    val falafelAuth = FalafelAuth(repo)
    val auth = MatveyAuth(authJwt) {
        if (!profile.isProd()) {
            falafelAuth.onAuthenticate(it)
        }
    }
    val serverConfig = config.server()
    runBlocking {
        MatveyBot(
            tgConfig = tgConfig,
            serverConfig = serverConfig,
            repo = repo,
            matveyAuth = auth,
            profile = profile
        ).start()
    }
    log.info { "Bot started. Launching server" }
    if (!profile.isProd()) {
        flywayMigrate(
            dataSource = ds,
            schema = "falafel",
            location = "classpath:db/falafel/migration",
            clean = dbConfig.clean(),
        )
    }
    startMatveyServer(
        serverConfig = serverConfig,
        profile = profile,
        auth = auth,
        repo = repo,
    )
}
