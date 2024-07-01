package uk.matvey.begit.server

import io.ktor.server.freemarker.FreeMarkerContent
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import uk.matvey.begit.athlete.Athlete
import uk.matvey.begit.athlete.AthleteRepo
import uk.matvey.begit.club.Club
import uk.matvey.begit.club.ClubRepo
import uk.matvey.begit.event.Event
import uk.matvey.begit.event.EventRepo
import java.util.UUID

fun Route.begitRouting(
    athleteRepo: AthleteRepo,
    clubRepo: ClubRepo,
    eventRepo: EventRepo,
) {
    data class BegitIndexModel(
        val athlete: Athlete,
        val events: List<Event>,
        val clubs: List<Club>,
    )
    get("/") {
        val athleteId = UUID.fromString("c63ac21f-8b20-4407-a4fb-7cf10681136e")
        val athlete = athleteRepo.getById(athleteId)
        val events = eventRepo.findAllByAthleteId(athleteId)
        val clubs = clubRepo.findAllByAthleteId(athleteId)
        val model = BegitIndexModel(athlete, events, clubs)
        call.respond(FreeMarkerContent("index.ftl", model), null)
    }
}