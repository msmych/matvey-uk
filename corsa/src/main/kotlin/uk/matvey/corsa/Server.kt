package uk.matvey.corsa

import com.typesafe.config.Config
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import mu.KotlinLogging
import uk.matvey.corsa.club.ClubService
import uk.matvey.slon.repo.Repo
import uk.matvey.utka.jwt.AuthJwt

private val log = KotlinLogging.logger("Server")

fun startServer(
    serverConfig: Config,
    repo: Repo,
    clubService: ClubService,
    authJwt: AuthJwt,
) {
    log.info { "Starting server" }
    embeddedServer(
        factory = Netty,
        port = serverConfig.getInt("port"),
        watchPaths = listOf("resources", "classes"),
        module = { serverModule(repo, clubService, authJwt) }
    ).start(wait = true)
}
