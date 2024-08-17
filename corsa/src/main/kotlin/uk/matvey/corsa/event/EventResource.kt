package uk.matvey.corsa.event

import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import uk.matvey.corsa.club.ClubService
import uk.matvey.corsa.event.EventSql.addEvent
import uk.matvey.corsa.event.EventSql.removeEvent
import uk.matvey.slon.repo.Repo
import uk.matvey.voron.KtorKit.pathParam
import uk.matvey.voron.KtorKit.queryParam
import uk.matvey.voron.KtorKit.receiveParamsMap
import uk.matvey.voron.KtorKit.respondFtl
import uk.matvey.voron.Resource
import java.time.LocalDate
import java.util.UUID

class EventResource(
    private val repo: Repo,
    private val clubService: ClubService,
) : Resource {

    override fun Route.routing() {
        route("/events") {
            getNewEventForm()
            addEvent()
            route("/{id}") {
                removeEvent()
            }
        }
    }

    private fun Route.getNewEventForm() {
        get("/new-event-form") {
            val clubId = call.queryParam("clubId")
            call.respondFtl("event/new-event-form", "clubId" to clubId)
        }
    }

    private fun Route.addEvent() {
        post {
            val params = call.receiveParamsMap()
            val clubId = UUID.fromString(params.getValue("clubId"))
            val name = params.getValue("name")
            val date = LocalDate.parse(params.getValue("date"))
            repo.access { a -> a.addEvent(clubId, name, date) }
            val clubDetails = clubService.getClubDetails(clubId)
            call.respondFtl("club/details", clubDetails)
        }
    }

    private fun Route.removeEvent() {
        delete {
            val eventId = UUID.fromString(call.pathParam("id"))
            repo.access { a -> a.removeEvent(eventId) }
            call.respond(OK)
        }
    }
}