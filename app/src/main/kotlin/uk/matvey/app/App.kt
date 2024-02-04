package uk.matvey.app

import freemarker.cache.ClassTemplateLoader
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.freemarker.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*
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