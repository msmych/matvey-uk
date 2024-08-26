package uk.matvey.corsa

import com.auth0.jwt.algorithms.Algorithm
import com.typesafe.config.ConfigFactory
import mu.KotlinLogging
import uk.matvey.corsa.club.ClubService
import uk.matvey.slon.repo.Repo
import uk.matvey.utka.jwt.AuthJwt

private val log = KotlinLogging.logger("Corsa")

fun main(vararg args: String) {
    log.info { "Hello, Corsa!" }
    val config = ConfigFactory.load("config/local.conf")
    val ds = dataSource(config.getConfig("dataSource"))
    migrate(ds, clean = args.any { it == "--clean" })
    val repo = Repo(ds)
    val clubService = ClubService(repo)
    val serverConfig = config.getConfig("server")
    val auth = AuthJwt(Algorithm.HMAC256(serverConfig.getString("jwtSecret")), "corsa")
    startTgBot(config.getConfig("tgBot"), serverConfig, auth, repo)
    startServer(serverConfig, repo, clubService, auth)
}
