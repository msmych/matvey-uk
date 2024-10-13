package uk.matvey.falafel.title

import kotlinx.serialization.Serializable
import java.time.Instant
import java.time.Year
import java.util.UUID

data class Title(
    val id: UUID,
    val state: State,
    val title: String,
    val directorName: String?,
    val releaseYear: Year?,
    val refs: Refs,
    val createdAt: Instant,
    val updatedAt: Instant,
) {

    enum class State {
        PENDING,
        ACTIVE,
    }

    @Serializable
    data class Refs(
        val tmdb: Int,
    )
}
