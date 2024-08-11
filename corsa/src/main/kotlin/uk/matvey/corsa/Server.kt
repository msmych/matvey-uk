package uk.matvey.corsa

import freemarker.cache.ClassTemplateLoader
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.freemarker.FreeMarker
import io.ktor.server.freemarker.FreeMarkerContent
import io.ktor.server.http.content.staticResources
import io.ktor.server.netty.Netty
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import mu.KotlinLogging

private val log = KotlinLogging.logger("Server")

fun startServer() {
    log.info { "Starting server" }
    embeddedServer(
        factory = Netty,
        port = 8080,
        watchPaths = listOf("resources", "classes"),
    ) {
        setupServer()
    }.start(wait = true)
}

fun Application.setupServer() {
    install(FreeMarker) {
        templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
    }
    routing {
        staticResources("/assets", "/assets")
        get("/healthcheck") {
            call.respondText("OK")
        }
        get {
            call.respond(FreeMarkerContent("index.ftl", null))
        }
    }
}