package uk.matvey.falafel.tmdb

import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import uk.matvey.falafel.title.TitleSql.addTitle
import uk.matvey.falafel.title.TitleSql.findAllByTmbdIds
import uk.matvey.kit.string.StringKit.toLocalDate
import uk.matvey.slon.repo.Repo
import uk.matvey.tmdb.TmdbClient
import uk.matvey.utka.Resource
import uk.matvey.utka.ktor.KtorKit.queryParam
import uk.matvey.utka.ktor.KtorKit.receiveParamsMap
import uk.matvey.utka.ktor.ftl.FreeMarkerKit.respondFtl

class TmdbResource(
    private val tmdbClient: TmdbClient,
    private val repo: Repo,
) : Resource {

    override fun Route.routing() {
        route("/tmdb") {
            getTmdbPage()
            saveTmdbMovieAsTitle()
            route("/search") {
                searchMovies()
            }
        }
    }

    private fun Route.getTmdbPage() {
        get {
            call.respondFtl("/falafel/tmdb/tmdb-page")
        }
    }

    private fun Route.saveTmdbMovieAsTitle() {
        post {
            val params = call.receiveParamsMap()
            val tmdbId = params.getValue("tmdbId").toInt()
            val details = tmdbClient.getMovie(tmdbId)
            val credits = tmdbClient.getMovieCredits(tmdbId)
            repo.access { a ->
                a.addTitle(
                    title = details.title,
                    directorName = credits.crew.find { item -> item.job == "Director" }?.name,
                    year = details.releaseDate?.toLocalDate()?.year,
                    tmdbId = tmdbId
                )
            }
            call.respond(OK)
        }
    }

    data class TmdbMovie(
        val id: String,
        val title: String,
        val releaseYear: String?,
        val saved: Boolean,
    )

    private fun Route.searchMovies() {
        get {
            val query = call.queryParam("q")
            val searchResult = tmdbClient.searchMovies(query)
            val savedTitles =
                repo.findAllByTmbdIds(searchResult.results.map { item -> item.id }).associateBy { it.refs.tmdb }
            call.respondFtl(
                "/falafel/tmdb/tmdb-movies-list",
                "movies" to searchResult.results.map {
                    TmdbMovie(
                        id = it.id.toString(),
                        title = it.title,
                        releaseYear = it.releaseDate.takeUnless { it.isBlank() }?.toLocalDate()?.year?.toString(),
                        saved = savedTitles.containsKey(it.id),
                    )
                },
            )
        }
    }
}
