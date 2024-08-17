package uk.matvey.corsa.event

import io.ktor.server.application.call
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import uk.matvey.slon.repo.Repo
import uk.matvey.voron.KtorKit.respondFtl
import uk.matvey.voron.Resource
import java.time.LocalDate
import java.util.UUID.randomUUID

class EventResource(
    private val repo: Repo,
) : Resource {

    override fun Route.routing() {
        route("/events") {
            getClubEvents()
        }
    }

    private fun Route.getClubEvents() {
        get {
            val events = listOf(
                "Monday morning run + Swim" to "2024-08-19",
                "Sunday run" to "2024-08-18",
            )
                .map { (name, date) -> Event(randomUUID(), randomUUID(), name, LocalDate.parse(date)) }
            call.respondFtl("event/events", mapOf("events" to events))
        }
    }
}