package uk.matvey.corsa.club

import io.ktor.server.application.call
import io.ktor.server.request.receiveParameters
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import uk.matvey.corsa.club.ClubSql.addClub
import uk.matvey.slon.repo.Repo
import uk.matvey.slon.repo.RepoKit.query
import uk.matvey.voron.KtorKit.respondFtl
import uk.matvey.voron.Resource

class ClubResource(
    private val repo: Repo,
) : Resource {

    override fun Route.routing() {
        route("/clubs") {
            getClubs()
            newClubForm()
            addClub()
        }
    }

    private fun Route.getClubs() {
        get {
            val clubs = repo.query("select * from clubs") {
                Club(it.uuid("id"), it.string("name"))
            }
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
            val params = call.receiveParameters()
            val club = repo.access { a -> a.addClub(params["name"]!!) }
            call.respondFtl("club/club", mapOf("club" to club))
        }
    }
}
