package uk.matvey.corsa.event

import uk.matvey.slon.RecordReader
import uk.matvey.slon.access.Access
import uk.matvey.slon.access.AccessKit.deleteFrom
import uk.matvey.slon.access.AccessKit.insertOneReturning
import uk.matvey.slon.param.DateParam.Companion.date
import uk.matvey.slon.param.PlainParam.Companion.now
import uk.matvey.slon.param.TextParam.Companion.text
import uk.matvey.slon.param.UuidParam.Companion.uuid
import java.time.LocalDate
import java.util.UUID

object EventSql {

    const val EVENTS = "events"

    const val ID = "id"
    const val CLUB_ID = "club_id"
    const val NAME = "name"
    const val DATE = "date"
    const val UPDATED_AT = "updated_at"

    fun Access.addEvent(clubId: UUID, name: String, date: LocalDate): Event {
        return insertOneReturning(EVENTS) {
            values(
                CLUB_ID to uuid(clubId),
                NAME to text(name),
                DATE to date(date),
                UPDATED_AT to now(),
            )
            returning { readEvent(it) }
        }
    }

    fun Access.removeEvent(eventId: UUID) {
        deleteFrom(EVENTS, "$ID = ?", uuid(eventId))
    }

    fun Access.getEventsByClubId(clubId: UUID): List<Event> {
        return query("select * from $EVENTS where $CLUB_ID = ?", listOf(uuid(clubId))) { readEvent(it) }
    }

    fun readEvent(r: RecordReader): Event {
        return Event(
            id = r.uuid(ID),
            clubId = r.uuid(CLUB_ID),
            name = r.string(NAME),
            date = r.localDate(DATE),
        )
    }
}