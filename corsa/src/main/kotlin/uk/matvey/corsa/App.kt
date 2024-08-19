package uk.matvey.corsa

import com.auth0.jwt.algorithms.Algorithm
import com.typesafe.config.ConfigFactory
import mu.KotlinLogging
import uk.matvey.corsa.club.ClubService
import uk.matvey.slon.repo.Repo

private val log = KotlinLogging.logger("Corsa")

fun main(vararg args: String) {
    log.info { "Hello, Corsa!" }
    val config = ConfigFactory.load("config/local.conf")
    val ds = dataSource(config.getConfig("dataSource"))
    migrate(ds, clean = args.any { it == "--clean" })
    val repo = Repo(ds)
    val clubService = ClubService(repo)
    val serverConfig = config.getConfig("server")
    val algorithm = Algorithm.HMAC256(serverConfig.getString("jwtSecret"))
    startTgBot(config.getConfig("tgBot"), serverConfig, algorithm, repo)
    startServer(serverConfig, repo, clubService, algorithm)
}
