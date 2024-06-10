package uk.matvey.begit.member

import java.time.Instant
import java.util.UUID

data class Member(
    val id: UUID,
    val name: String,
    val refs: Refs,
    val createdAt: Instant,
    val updatedAt: Instant,
) {

    data class Refs(
        val tgId: Long,
    )
}