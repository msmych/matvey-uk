package uk.matvey.corsa.event

import java.time.LocalDate
import java.util.UUID

data class Event(
    val id: UUID,
    val name: String,
    val date: LocalDate,
)