package uk.matvey.begit.event

import io.ktor.server.freemarker.FreeMarkerContent
import io.ktor.server.request.receiveParameters
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset
import java.util.UUID

fun Route.eventRouting(
    eventRepo: EventRepo,
) {
    route("/events") {
        get {
            val clubId = requireNotNull(call.queryParameters["clubId"]).let(UUID::fromString)
            val events = eventRepo.findAllByClubId(clubId)
            call.respond(FreeMarkerContent("event/events.ftl", mapOf("clubId" to clubId, "events" to events)), null)
        }
        post {
            val athleteId = UUID.fromString("c63ac21f-8b20-4407-a4fb-7cf10681136e")
            val params = call.receiveParameters()
            val clubId = requireNotNull(params["clubId"]).let(UUID::fromString)
            val title = requireNotNull(params["title"])
            val date = requireNotNull(params["date"]).let(LocalDate::parse)
            val time = requireNotNull(params["time"]).let(LocalTime::parse)
            eventRepo.add(clubId, athleteId, title, date.atTime(time).toInstant(ZoneOffset.UTC))
            val events = eventRepo.findAllByClubId(clubId)
            call.respond(FreeMarkerContent("event/events.ftl", mapOf("clubId" to clubId, "events" to events)), null)
        }
        route("/new") {
            get {
                val clubId = requireNotNull(call.queryParameters["clubId"]).let(UUID::fromString)
                call.respond(FreeMarkerContent("event/new-event-form.ftl", mapOf("clubId" to clubId)), null)
            }
            delete {
                val clubId = requireNotNull(call.queryParameters["clubId"]).let(UUID::fromString)
                call.respond(FreeMarkerContent("event/new-event-button.ftl", mapOf("clubId" to clubId)), null)
            }
        }
    }
}