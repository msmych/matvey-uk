package uk.matvey.falafel.title

import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.server.application.call
import io.ktor.server.auth.principal
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import uk.matvey.app.account.AccountPrincipal
import uk.matvey.falafel.balance.AccountBalance
import uk.matvey.falafel.balance.BalanceSql.ensureBalance
import uk.matvey.falafel.title.TitleSql.addTitle
import uk.matvey.falafel.title.TitleSql.getActiveTitles
import uk.matvey.kit.string.StringKit.toLocalDate
import uk.matvey.slon.repo.Repo
import uk.matvey.tmdb.TmdbClient
import uk.matvey.utka.Resource
import uk.matvey.utka.ktor.KtorKit.queryParam
import uk.matvey.utka.ktor.KtorKit.receiveParamsMap
import uk.matvey.utka.ktor.ftl.FreeMarkerKit.respondFtl

class TitleResource(
    private val repo: Repo,
    private val tmdbClient: TmdbClient,
) : Resource {

    override fun Route.routing() {
        route("/titles") {
            route("/search-tmdb") {
                searchTmdbTitles()
            }
            getTitles()
            getNewTitleForm()
            addTitleFromTmdb()
        }
    }

    private fun Route.searchTmdbTitles() {
        get {
            val query = call.queryParam("q")
            val searchResult = tmdbClient.searchMovies(query)
            call.respondFtl("/falafel/titles/tmdb-titles", "titles" to searchResult.results)
        }
    }

    private fun Route.getTitles() {
        get {
            val titles = repo.getActiveTitles()
            val account = call.principal<AccountPrincipal>()?.let {
                val balance = repo.access { a -> a.ensureBalance(it.id) }
                AccountBalance.from(it, balance)
            }
            call.respondFtl(
                "/falafel/titles/titles",
                "titles" to titles,
                "account" to account,
            )
        }
    }

    private fun Route.getNewTitleForm() {
        get("/new-title-form") {
            call.respondFtl("/falafel/titles/new-title-form")
        }
    }

    data class TmdbTitleDetails(
        val id: Int,
        val title: String,
        val originalTitle: String,
        val year: Int?,
        val director: String?,
    )

    private fun Route.addTitleFromTmdb() {
        post {
            val params = call.receiveParamsMap()
            val tmdbId = params.getValue("tmdbId").toInt()
            val movieDetails = tmdbClient.getMovie(tmdbId)
            val credits = tmdbClient.getMovieCredits(tmdbId)
            val title = TmdbTitleDetails(
                movieDetails.id,
                movieDetails.title,
                movieDetails.originalTitle,
                movieDetails.releaseDate?.toLocalDate()?.year,
                credits.crew.find { it.job == "Director" }?.name,
            )
            repo.access { a -> a.addTitle(title.title, title.director, title.year, title.id) }
            call.respond(OK)
        }
    }
}
