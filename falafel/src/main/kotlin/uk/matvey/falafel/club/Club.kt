package uk.matvey.falafel.club

import kotlinx.serialization.Serializable
import uk.matvey.falafel.event.Event
import java.time.Instant
import java.util.UUID

data class Club(
    val id: UUID,
    val name: String,
    val refs: Refs,
    val defaultEventType: Event.Type?,
    val createdAt: Instant,
    val updatedAt: Instant,
) {

    @Serializable
    data class Refs(
        val tg: String? = null,
    )
}
