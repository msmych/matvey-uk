package uk.matvey.falafel.tag

import java.time.Instant
import java.util.UUID

data class Tag(
    val name: String,
    val titleId: UUID,
    val balanceId: UUID,
    val createdAt: Instant,
)
