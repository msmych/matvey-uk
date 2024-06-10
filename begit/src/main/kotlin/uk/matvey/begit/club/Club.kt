package uk.matvey.begit.club

import kotlinx.serialization.Serializable
import java.time.Instant
import java.util.UUID

data class Club(
    val id: UUID,
    val name: String,
    val description: String?,
    val refs: Refs,
    val createdAt: Instant,
    val updatedAt: Instant,
) {

    @Serializable
    data class Refs(
        val tgId: Long,
    )
}