package uk.matvey.falafel.tmdb

import io.ktor.server.request.header
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import uk.matvey.falafel.FalafelAuth
import uk.matvey.falafel.FalafelFtl
import uk.matvey.falafel.title.TitleSql.addTitle
import uk.matvey.falafel.title.TitleSql.findAllByTmbdIds
import uk.matvey.falafel.tmdb.TmdbFtl.TmdbMovie
import uk.matvey.kit.string.StringKit.toLocalDate
import uk.matvey.slon.repo.Repo
import uk.matvey.tmdb.TmdbClient
import uk.matvey.utka.Resource
import uk.matvey.utka.ktor.KtorKit.queryParam
import uk.matvey.utka.ktor.KtorKit.receiveParamsMap
import uk.matvey.utka.ktor.ftl.FreeMarkerKit.respondFtl

class TmdbResource(
    private val falafelAuth: FalafelAuth,
    private val falafelFtl: FalafelFtl,
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
            if (call.request.header("HX-Request") != "true") {
                return@get falafelFtl.respondIndex(call, "/falafel/tmdb")
            }
            val account = falafelAuth.getAccountBalance(call)
            call.respondFtl("/falafel/tmdb/tmdb-page", "account" to account)
        }
    }

    private fun Route.saveTmdbMovieAsTitle() {
        post {
            val params = call.receiveParamsMap()
            val tmdbId = params.getValue("tmdbId").toInt()
            val (details, credits) = tmdbClient.getMovie(tmdbId) to tmdbClient.getMovieCredits(tmdbId)
            val title = repo.access { a ->
                a.addTitle(
                    title = details.title,
                    directorName = credits.crew.find { item -> item.job == "Director" }?.name,
                    year = details.releaseDate?.toLocalDate()?.year,
                    tmdbId = tmdbId
                )
            }
            call.respondFtl(
                "/falafel/tmdb/tmdb-movie", "movie" to TmdbMovie(
                    id = details.id.toString(),
                    title = details.title,
                    releaseYear = details.releaseDate?.toLocalDate()?.year?.toString(),
                    titleId = title.id
                )
            )
        }
    }

    private fun Route.searchMovies() {
        get {
            val query = call.queryParam("q")
            val searchResult = tmdbClient.searchMovies(query)
            val savedTitles = repo.findAllByTmbdIds(searchResult.results.map { item -> item.id })
                .associateBy { it.refs.tmdb }
            call.respondFtl(
                "/falafel/tmdb/tmdb-movies-list",
                "movies" to searchResult.results.map {
                    TmdbMovie(
                        id = it.id.toString(),
                        title = it.title,
                        releaseYear = it.releaseDate.takeUnless { it.isBlank() }?.toLocalDate()?.year?.toString(),
                        titleId = savedTitles[it.id]?.id,
                    )
                },
            )
        }
    }
}
