package uk.matvey.falafel.club

import io.ktor.server.html.respondHtml
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import kotlinx.html.body
import uk.matvey.falafel.FalafelAuth
import uk.matvey.falafel.club.ClubHtml.clubsPage
import uk.matvey.falafel.club.ClubSql.CLUBS
import uk.matvey.falafel.club.ClubSql.getClubById
import uk.matvey.kit.string.StringKit.toUuid
import uk.matvey.slon.repo.Repo
import uk.matvey.slon.repo.RepoKit.insertOneInto
import uk.matvey.slon.value.Pg
import uk.matvey.utka.Resource
import uk.matvey.utka.ktor.KtorKit.receiveParamsMap
import uk.matvey.utka.ktor.ftl.FreeMarkerKit.respondFtl

class ClubResource(
    private val falafelAuth: FalafelAuth,
    private val repo: Repo,
    private val clubService: ClubService,
) : Resource {

    override fun Route.routing() {
        clubRouting()
        route("/clubs/{clubId}") {
            clubRouting()
        }
    }

    private fun Route.clubRouting() {
        route("/clubs") {
            getClubs()
            getNewClubForm()
            addClub()
        }
    }

    private fun Route.getClubs() {
        get {
            val account = falafelAuth.getAccountBalance(call)
            val clubs = clubService.getClubs()
            val clubId = call.pathParameters["clubId"]?.toUuid()
            val club = clubId?.let { repo.access { a -> a.getClubById(it) } }
            call.respondHtml {
                body {
                    clubsPage(clubs, account, club)
                }
            }
        }
    }

    private fun Route.getNewClubForm() {
        get("/new-club-form") {
            call.respondFtl("falafel/clubs/new-club-form")
        }
    }

    private fun Route.addClub() {
        post {
            val param = call.receiveParamsMap()
            repo.insertOneInto(CLUBS) {
                set("name", param.getValue("name"))
                set("updated_at", Pg.now())
            }
            val clubs = clubService.getClubs()
            call.respondFtl("falafel/clubs/clubs", "clubs" to clubs)
        }
    }
}
