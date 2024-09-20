package uk.matvey.falafel.balance

import java.time.Instant
import java.util.UUID

data class Balance(
    val id: UUID,
    val accountId: UUID,
    val createdAt: Instant,
    val updatedAt: Instant,
) {
}
