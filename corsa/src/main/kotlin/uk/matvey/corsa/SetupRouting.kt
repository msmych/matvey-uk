package uk.matvey.corsa

import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.freemarker.FreeMarkerContent
import io.ktor.server.http.content.staticResources
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import uk.matvey.corsa.club.ClubResource
import uk.matvey.corsa.event.EventResource

fun Application.setupRouting() {
    routing {
        staticResources("/assets", "/assets")
        get("/healthcheck") {
            call.respondText("OK")
        }
        get {
            call.respond(FreeMarkerContent("index.ftl", null))
        }
        with(ClubResource()) { routing() }
        with(EventResource()) { routing() }
    }
}