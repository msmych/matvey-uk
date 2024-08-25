package uk.matvey.corsa.club

import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.http.HttpStatusCode.Companion.Unauthorized
import io.ktor.server.application.call
import io.ktor.server.auth.principal
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import uk.matvey.corsa.ServerAuth.AthletePrincipal
import uk.matvey.corsa.club.ClubSql.addClub
import uk.matvey.corsa.club.ClubSql.removeClub
import uk.matvey.corsa.event.EventSql.getEventsByClubId
import uk.matvey.corsa.event.EventSql.removeEvent
import uk.matvey.kit.string.StringKit.toUuid
import uk.matvey.slon.repo.Repo
import uk.matvey.utka.Resource
import uk.matvey.utka.ktor.KtorKit.pathParam
import uk.matvey.utka.ktor.KtorKit.receiveParamsMap
import uk.matvey.utka.ktor.ftl.FreeMarkerKit.respondFtl

class ClubResource(
    private val repo: Repo,
    private val clubService: ClubService,
) : Resource {

    override fun Route.routing() {
        route("/clubs") {
            getClubs()
            newClubForm()
            addClub()
            route("/{id}") {
                getClubDetails()
                removeClub()
            }
        }
    }

    private fun Route.getClubs() {
        get {
            val principal = call.principal<AthletePrincipal>() ?: return@get call.respond(Unauthorized)
            val clubs = clubService.getAthleteClubs(principal.id)
            call.respondFtl("club/clubs", mapOf("clubs" to clubs))
        }
    }

    private fun Route.newClubForm() {
        get("/new-club-form") {
            call.respondFtl("club/new-club-form")
        }
    }

    private fun Route.addClub() {
        post {
            val params = call.receiveParamsMap()
            val athlete = call.principal<AthletePrincipal>() ?: return@post call.respond(Unauthorized)
            repo.access { a -> a.addClub(params.getValue("name"), athlete.id) }
            val clubs = clubService.getAthleteClubs(athlete.id)
            call.respondFtl("club/clubs", mapOf("clubs" to clubs))
        }
    }

    private fun Route.removeClub() {
        delete {
            val clubId = call.pathParam("id").toUuid()
            repo.access { a ->
                a.getEventsByClubId(clubId).forEach { event ->
                    a.removeEvent(event.id)
                }
                a.removeClub(clubId)
            }
            call.respond(OK)
        }
    }

    private fun Route.getClubDetails() {
        get {
            val clubId = call.pathParam("id").toUuid()
            val clubDetails = clubService.getClubDetails(clubId)
            call.respondFtl("club/details", clubDetails)
        }
    }
}
