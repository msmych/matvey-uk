package uk.matvey.app

import com.auth0.jwt.algorithms.Algorithm
import com.typesafe.config.ConfigFactory
import kotlinx.coroutines.runBlocking
import uk.matvey.app.config.AppConfig
import mu.KotlinLogging
import uk.matvey.slon.FlywayKit.flywayMigrate
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
        clean = dbConfig.clean(),
    ) {
        placeholders(mapOf("tgAdminId" to tgConfig.adminId().toString()))
    }

    val repo = Repo(ds)
    val authJwt = AuthJwt(Algorithm.HMAC256(config.jwtSecret()), "matvey")
    val auth = MatveyAuth(authJwt)
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
    startServer(
        serverConfig = serverConfig,
        profile = profile,
        auth = auth,
        repo = repo,
    )
}
