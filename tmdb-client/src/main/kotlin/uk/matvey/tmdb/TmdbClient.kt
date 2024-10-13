package uk.matvey.tmdb

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import uk.matvey.kit.json.JsonKit.JSON

class TmdbClient(
    token: String,
) {

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(JSON)
        }
        defaultRequest {
            url("https://api.themoviedb.org/3")
            contentType(ContentType.Application.Json)
            bearerAuth(token)
        }
    }

    suspend fun searchMovies(query: String): SearchMoviesResponse {
        return client.get("/3/search/movie") {
            url.parameters.append("query", query)
        }.body()
    }

    suspend fun getMovie(id: Int): Movie {
        return client.get("/3/movie/$id").body()
    }

    suspend fun getMovieCredits(movieId: Int): MovieCreditsResponse {
        return client.get("/3/movie/$movieId/credits").body()
    }
}
