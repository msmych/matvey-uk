package uk.matvey.tmdb

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MovieCreditsResponse(
    val id: Int,
    val cast: List<CastItem>,
    val crew: List<CrewItem>,
) {
    @Serializable
    data class CastItem(
        val id: Int,
        val name: String,
        @SerialName("original_name")
        val originalName: String,
    )

    @Serializable
    data class CrewItem(
        val id: Int,
        val name: String,
        @SerialName("original_name")
        val originalName: String,
        val job: String,
    )
}
