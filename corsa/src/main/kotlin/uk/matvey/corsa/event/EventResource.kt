package uk.matvey.corsa.event

import io.ktor.server.application.call
import io.ktor.server.freemarker.FreeMarkerContent
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import java.time.LocalDate
import java.util.UUID.randomUUID

class EventResource {

    fun Route.routing() {
        route("/events") {
            getEvents()
        }
    }

    private fun Route.getEvents() {
        get {
            val events = listOf(
                "Monday morning run + Swim" to "2024-08-19",
                "Sunday run" to "2024-08-18",
            )
                .map { (name, date) -> Event(randomUUID(), name, LocalDate.parse(date)) }
            call.respond(FreeMarkerContent("event/events.ftl", mapOf("events" to events)))
        }
    }
}