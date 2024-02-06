package uk.matvey.app

import freemarker.cache.ClassTemplateLoader
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.freemarker.FreeMarker
import io.ktor.server.freemarker.FreeMarkerContent
import io.ktor.server.netty.Netty
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import java.util.UUID
import java.util.UUID.randomUUID

fun main() {
    embeddedServer(Netty, 8080) {
        install(FreeMarker) {
            templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
        }
        routing {
            get("/") {
                data class Model(val id: UUID, val username: String)

                call.respond(FreeMarkerContent("index.ftl", Model(randomUUID(), "Username")))
            }
        }
    }
        .start(wait = true)
}
