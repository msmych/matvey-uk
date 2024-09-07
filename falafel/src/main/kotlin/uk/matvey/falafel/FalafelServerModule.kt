package uk.matvey.falafel

import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import uk.matvey.falafel.club.ClubResource
import uk.matvey.utka.ktor.ftl.FreeMarkerKit.respondFtl

fun Application.falafelServerModule() {
    val resources = listOf(
        ClubResource(),
    )
    routing {
        route("/falafel") {
            get {
                call.respondFtl("falafel/index")
            }
            resources.forEach { with(it) { routing() } }
        }
    }
}
