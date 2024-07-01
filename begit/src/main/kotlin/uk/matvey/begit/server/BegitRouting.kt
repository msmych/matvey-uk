package uk.matvey.begit.server

import io.ktor.server.freemarker.FreeMarkerContent
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import uk.matvey.begit.athlete.AthleteRepo
import uk.matvey.begit.club.ClubRepo
import java.util.UUID

fun Route.begitRouting(
    athleteRepo: AthleteRepo,
    clubRepo: ClubRepo,
) {
    get("/") {
        call.respond(FreeMarkerContent("index.ftl", null), null)
    }
    get("/me") {
        val athleteId = UUID.fromString("c63ac21f-8b20-4407-a4fb-7cf10681136e")
        val athlete = athleteRepo.getById(athleteId)
        call.respond(FreeMarkerContent("athlete/athlete.ftl", mapOf("athlete" to athlete)), null)
    }
    get("/clubs") {
        val athleteId = UUID.fromString("c63ac21f-8b20-4407-a4fb-7cf10681136e")
        val clubs = clubRepo.findAllByAthleteId(athleteId)
        call.respond(FreeMarkerContent("club/clubs.ftl", mapOf("clubs" to clubs)), null)
    }
}