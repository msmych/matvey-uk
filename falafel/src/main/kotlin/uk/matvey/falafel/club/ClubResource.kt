package uk.matvey.falafel.club

import io.ktor.server.application.call
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import uk.matvey.utka.Resource
import uk.matvey.utka.ktor.ftl.FreeMarkerKit.respondFtl

class ClubResource : Resource {

    override fun Route.routing() {
        route("/clubs") {
            getNewClubForm()
        }
    }

    private fun Route.getNewClubForm() {
        get("/new-club-form") {
            call.respondFtl("falafel/clubs/new-club-form")
        }
    }
}
