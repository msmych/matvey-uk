package uk.matvey.falafel.title

import io.ktor.server.application.call
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import uk.matvey.falafel.FalafelAuth
import uk.matvey.falafel.title.TitleSql.searchActiveTitles
import uk.matvey.slon.repo.Repo
import uk.matvey.utka.Resource
import uk.matvey.utka.ktor.KtorKit.queryParam
import uk.matvey.utka.ktor.ftl.FreeMarkerKit.respondFtl

class TitleResource(
    private val falafelAuth: FalafelAuth,
    private val repo: Repo,
) : Resource {

    override fun Route.routing() {
        route("/titles") {
            getTitlesPage()
            route("/search") {
                searchTitles()
            }
            getNewTitleForm()
        }
    }

    private fun Route.getTitlesPage() {
        get {
            val accountBalance = falafelAuth.getAccountBalance(call)
            call.respondFtl("/falafel/titles/titles-page", "account" to accountBalance)
        }
    }

    private fun Route.searchTitles() {
        get {
            val query = call.queryParam("q")
            val titles = repo.searchActiveTitles(query)
            call.respondFtl("/falafel/titles/titles-list", "titles" to titles)
        }
    }

    private fun Route.getNewTitleForm() {
        get("/new-title-form") {
            call.respondFtl("/falafel/titles/new-title-form")
        }
    }
}
