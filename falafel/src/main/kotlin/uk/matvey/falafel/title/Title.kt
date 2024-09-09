package uk.matvey.falafel.title

import kotlinx.serialization.Serializable
import java.time.Instant
import java.util.UUID

data class Title(
    val id: UUID,
    val state: State,
    val title: String,
    val clubId: UUID?,
    val refs: Refs,
    val createdBy: UUID?,
    val createdAt: Instant,
    val updatedAt: Instant,
) {

    enum class State {
        ACTIVE,
    }

    @Serializable
    data class Refs(
        val letterboxd: String? = null,
        val imdb: String? = null,
        val kp: String? = null,
    )
}
