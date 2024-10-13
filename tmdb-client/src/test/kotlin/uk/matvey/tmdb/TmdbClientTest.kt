package uk.matvey.tmdb

import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable

class TmdbClientTest {

    private fun client() = TmdbClient(System.getenv("TMDB_TOKEN"))

    @Test
    @EnabledIfEnvironmentVariable(named = "TMDB_TOKEN", matches = ".*")
    fun `should collect movie details`() = runTest {
        // given
        val client = client()

        // when
        val movies = client.searchMovies("pulp fiction")

        // then
        assertThat(movies.results).isNotEmpty
        assertThat(movies.results[0].title).isEqualTo("Pulp Fiction")

        // given
        val movieId = movies.results[0].id

        // when
        val credits = client.getMovieCredits(movieId)

        // then
        assertThat(credits.crew).isNotEmpty
        assertThat(credits.crew.filter { it.job == "Director" }).hasOnlyOneElementSatisfying {
            assertThat(it.name).isEqualTo("Quentin Tarantino")
        }
    }
}
