package uk.matvey.tmdb

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SearchMoviesResponse(
    val results: List<Item>,
    val page: Int,
    @SerialName("total_pages")
    val totalPages: Int,
    @SerialName("total_results")
    val totalResults: Int,
) {

    @Serializable
    data class Item(
        val id: Int,
        val title: String,
        @SerialName("original_title")
        val originalTitle: String,
        @SerialName("release_date")
        val releaseDate: String,
        @SerialName("poster_path")
        val posterPath: String?,
    )
}
