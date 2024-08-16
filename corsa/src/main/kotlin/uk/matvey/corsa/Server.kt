package uk.matvey.corsa

import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import mu.KotlinLogging
import uk.matvey.slon.repo.Repo

private val log = KotlinLogging.logger("Server")

fun startServer(repo: Repo) {
    log.info { "Starting server" }
    embeddedServer(
        factory = Netty,
        port = 8080,
        watchPaths = listOf("resources", "classes"),
        module = { serverModule(repo) }
    ).start(wait = true)
}
