package uk.matvey.begit.event

import uk.matvey.begit.event.EventSql.EVENTS
import uk.matvey.begit.event.EventSql.ID
import uk.matvey.begit.event.EventSql.readEvent
import uk.matvey.slon.InsertBuilder.Companion.insertInto
import uk.matvey.slon.access.Access
import uk.matvey.slon.param.UuidParam.Companion.uuid
import uk.matvey.slon.query.update.DeleteQuery.Builder.Companion.deleteFrom
import java.util.UUID

object EventParticipantSql {

    const val EVENT_PARTICIPANTS = "begit.event_participants"

    const val EVENT_ID = "event_id"
    const val ATHLETE_ID = "athlete_id"

    fun Access.addEventParticipant(eventId: UUID, athleteId: UUID): Boolean {
        return execute(
            insertInto(EVENT_PARTICIPANTS)
                .set(
                    EVENT_ID to uuid(eventId),
                    ATHLETE_ID to uuid(athleteId),
                )
                .onConflictDoNothing()
                .build()
        ) > 0
    }

    fun Access.deleteEventParticipant(eventId: UUID, athleteId: UUID): Boolean {
        return execute(
            deleteFrom(EVENT_PARTICIPANTS)
                .where(
                    "$EVENT_ID = ? and $ATHLETE_ID = ?", uuid(eventId), uuid(athleteId)
                ),
        ) > 0
    }

    fun Access.countEventParticipants(eventId: UUID): Int {
        return queryOne(
            "select count(*) from $EVENT_PARTICIPANTS where $EVENT_ID = ?",
            listOf(uuid(eventId))
        ) { it.int(1) }
    }

    fun Access.findAllEventsByAthleteId(athleteId: UUID): List<Event> {
        return query(
            "select e.* from $EVENT_PARTICIPANTS ep join $EVENTS e on ep.$EVENT_ID = e.$ID where ep.$ATHLETE_ID = ?",
            listOf(uuid(athleteId))
        ) { it.readEvent() }
    }
}