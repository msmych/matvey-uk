package uk.matvey.begit.event

import uk.matvey.slon.Access
import uk.matvey.slon.InsertBuilder.Companion.insertInto
import uk.matvey.slon.param.UuidParam.Companion.uuid
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

    fun Access.countEventParticipants(eventId: UUID): Int {
        return queryOne(
            "select count(*) from $EVENT_PARTICIPANTS where $EVENT_ID = ?",
            listOf(uuid(eventId))
        ) { it.int(1) }
    }
}