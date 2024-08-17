package uk.matvey.corsa.event

import uk.matvey.slon.RecordReader
import uk.matvey.slon.access.Access
import uk.matvey.slon.param.UuidParam.Companion.uuid
import java.util.UUID

object EventSql {

    const val EVENTS = "events"

    const val CLUB_ID = "club_id"

    fun Access.getEventsByClubId(clubId: UUID): List<Event> {
        return query("select * from $EVENTS where $CLUB_ID = ?", listOf(uuid(clubId))) { readEvent(it) }
    }

    fun readEvent(r: RecordReader): Event {
        return Event(
            id = r.uuid("id"),
            clubId = r.uuid("club_id"),
            name = r.string("name"),
            date = r.localDate("date"),
        )
    }
}