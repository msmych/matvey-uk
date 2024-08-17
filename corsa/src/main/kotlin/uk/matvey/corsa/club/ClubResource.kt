package uk.matvey.corsa.club

import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import uk.matvey.corsa.club.ClubSql.addClub
import uk.matvey.corsa.club.ClubSql.readClub
import uk.matvey.corsa.club.ClubSql.removeClub
import uk.matvey.slon.repo.Repo
import uk.matvey.slon.repo.RepoKit.query
import uk.matvey.voron.KtorKit.pathParam
import uk.matvey.voron.KtorKit.receiveParamsMap
import uk.matvey.voron.KtorKit.respondFtl
import uk.matvey.voron.Resource
import java.util.UUID

class ClubResource(
    private val repo: Repo,
) : Resource {

    override fun Route.routing() {
        route("/clubs") {
            getClubs()
            newClubForm()
            addClub()
            route("/{id}") {
                removeClub()
            }
        }
    }

    private fun Route.getClubs() {
        get {
            val clubs = repo.query("select * from clubs") { readClub(it) }
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
            repo.access { a -> a.addClub(params.getValue("name")) }
            val clubs = repo.query("select * from clubs") { readClub(it) }
            call.respondFtl("club/clubs", mapOf("clubs" to clubs))
        }
    }

    private fun Route.removeClub() {
        delete {
            val clubId = UUID.fromString(call.pathParam("id"))
            repo.access { a -> a.removeClub(clubId) }
            call.respond(OK)
        }
    }
}
