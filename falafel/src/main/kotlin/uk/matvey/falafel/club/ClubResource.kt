package uk.matvey.falafel.club

import io.ktor.server.application.call
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import uk.matvey.falafel.club.ClubSql.CLUBS
import uk.matvey.slon.query.InsertOneQueryBuilder.Companion.insertOneInto
import uk.matvey.slon.repo.Repo
import uk.matvey.slon.value.Pg
import uk.matvey.utka.Resource
import uk.matvey.utka.ktor.KtorKit.receiveParamsMap
import uk.matvey.utka.ktor.ftl.FreeMarkerKit.respondFtl

class ClubResource(
    private val repo: Repo,
    private val clubService: ClubService,
) : Resource {

    override fun Route.routing() {
        route("/clubs") {
            getClubs()
            getNewClubForm()
            addClub()
        }
    }

    private fun Route.getClubs() {
        get {
            val clubs = clubService.getClubs()
            call.respondFtl("falafel/clubs/clubs", "clubs" to clubs)
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
            repo.access { a ->
                a.execute(
                    insertOneInto(CLUBS) {
                        set("name", param.getValue("name"))
                        set("updated_at", Pg.now())
                    }
                )
            }
            val clubs = clubService.getClubs()
            call.respondFtl("falafel/clubs/clubs", "clubs" to clubs)
        }
    }
}
