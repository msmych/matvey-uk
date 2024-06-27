package uk.matvey.begit.athlete

import kotlinx.serialization.Serializable
import java.time.Instant
import java.util.UUID

data class Athlete(
    val id: UUID,
    val name: String,
    val refs: Refs,
    val createdAt: Instant,
    val updatedAt: Instant,
) {

    @Serializable
    data class Refs(
        val tgChatId: Long,
    )
}