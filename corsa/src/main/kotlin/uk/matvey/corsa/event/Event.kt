package uk.matvey.corsa.event

import java.time.Instant
import java.time.LocalDate
import java.util.UUID

data class Event(
    val id: UUID,
    val clubId: UUID,
    val name: String,
    val date: LocalDate,
    val dateTime: Instant?,
)