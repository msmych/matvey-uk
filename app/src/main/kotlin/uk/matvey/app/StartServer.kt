package uk.matvey.app

import freemarker.cache.ClassTemplateLoader
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.freemarker.FreeMarker
import io.ktor.server.freemarker.FreeMarkerContent
import io.ktor.server.http.content.staticResources
import io.ktor.server.netty.Netty
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

fun startServer(wait: Boolean) {
    embeddedServer(Netty, port = 8080, watchPaths = listOf("resources")) {
        install(FreeMarker) {
            templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
        }
        routing {
            staticResources("/assets", "/assets")
            get("/healthcheck") {
                call.respond("OK")
            }
            get("/") {
                call.respond(FreeMarkerContent("index.ftl", null))
            }
        }
    }
        .start(wait = wait)
}
