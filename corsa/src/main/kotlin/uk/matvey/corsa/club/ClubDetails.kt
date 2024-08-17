package uk.matvey.corsa.club

import uk.matvey.corsa.event.Event

data class ClubDetails(
    val club: Club,
    val events: List<Event>,
)