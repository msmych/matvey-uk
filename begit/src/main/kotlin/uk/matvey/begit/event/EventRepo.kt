package uk.matvey.begit.event

import uk.matvey.begit.event.EventParticipantSql.findAllEventsByAthleteId
import uk.matvey.begit.event.EventSql.findAllEventsByClubId
import uk.matvey.begit.event.EventSql.getEventById
import uk.matvey.begit.event.EventSql.insertEvent
import uk.matvey.slon.Repo
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.UUID

class EventRepo(
    private val repo: Repo,
) {

    fun add(clubId: UUID, organizedBy: UUID, title: String, dateTime: Instant) {
        repo.access { a -> a.insertEvent(clubId, organizedBy, title, LocalDate.ofInstant(dateTime, ZoneId.of("UTC")), dateTime) }
    }

    fun getById(id: UUID): Event {
        return repo.access { a -> a.getEventById(id) }
    }

    fun findAllByClubId(clubId: UUID): List<Event> {
        return repo.access { a -> a.findAllEventsByClubId(clubId) }
    }

    fun findAllByAthleteId(athleteId: UUID): List<Event> {
        return repo.access { a -> a.findAllEventsByAthleteId(athleteId) }
    }
}