package uk.matvey.tmdb

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Movie(
    val id: Int,
    val title: String,
    @SerialName("original_title")
    val originalTitle: String,
    @SerialName("release_date")
    val releaseDate: String?,
)
