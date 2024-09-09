package uk.matvey.falafel

import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import uk.matvey.falafel.club.ClubResource
import uk.matvey.falafel.club.ClubService
import uk.matvey.falafel.title.TitleResource
import uk.matvey.falafel.title.TitleService
import uk.matvey.slon.repo.Repo
import uk.matvey.utka.ktor.ftl.FreeMarkerKit.respondFtl

fun Application.falafelServerModule(
    repo: Repo,
) {
    val clubService = ClubService(repo)
    val titleService = TitleService(repo)
    val resources = listOf(
        ClubResource(repo, clubService),
        TitleResource(repo, titleService),
    )
    routing {
        route("/falafel") {
            get {
                call.respondFtl("falafel/index")
            }
            authenticate("jwt") {
                resources.forEach { with(it) { routing() } }
            }
        }
    }
}
