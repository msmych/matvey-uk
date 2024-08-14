package uk.matvey.corsa.club

import io.ktor.server.application.call
import io.ktor.server.freemarker.FreeMarkerContent
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import uk.matvey.voron.Resource
import java.util.UUID.randomUUID

class ClubResource : Resource {

    override fun Route.routing() {
        route("/clubs") {
            getClubs()
        }
    }

    private fun Route.getClubs() {
        get {
            val clubs = listOf(
                "Dream Chasers",
            )
                .map { Club(randomUUID(), it) }
            call.respond(FreeMarkerContent("club/clubs.ftl", mapOf("clubs" to clubs)))
        }
    }
}