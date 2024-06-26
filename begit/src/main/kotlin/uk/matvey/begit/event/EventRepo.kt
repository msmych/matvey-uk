package uk.matvey.begit.event

import uk.matvey.begit.event.EventParticipantSql.findAllEventsByAthleteId
import uk.matvey.begit.event.EventSql.getEventById
import uk.matvey.slon.Repo
import java.util.UUID

class EventRepo(
    private val repo: Repo,
) {

    fun getById(id: UUID): Event {
        return repo.access { a -> a.getEventById(id) }
    }

    fun findAllByAthleteId(athleteId: UUID): List<Event> {
        return repo.access { a -> a.findAllEventsByAthleteId(athleteId) }
    }
}