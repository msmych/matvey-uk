package uk.matvey.corsa

import io.ktor.server.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import mu.KotlinLogging

private val log = KotlinLogging.logger("Server")

fun startServer() {
    log.info { "Starting server" }
    embeddedServer(
        factory = Netty,
        port = 8080,
        watchPaths = listOf("resources", "classes"),
        module = Application::setupServer
    ).start(wait = true)
}
