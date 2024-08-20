package uk.matvey.corsa.event

import uk.matvey.corsa.CorsaSql.ID
import uk.matvey.corsa.CorsaSql.UPDATED_AT
import uk.matvey.slon.RecordReader
import uk.matvey.slon.access.Access
import uk.matvey.slon.access.AccessKit.deleteFrom
import uk.matvey.slon.access.AccessKit.insertReturningOne
import uk.matvey.slon.param.DateParam.Companion.date
import uk.matvey.slon.param.PlainParam.Companion.now
import uk.matvey.slon.param.TextParam.Companion.text
import uk.matvey.slon.param.TimestampParam.Companion.timestamp
import uk.matvey.slon.param.UuidParam.Companion.uuid
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset.UTC
import java.util.UUID

object EventSql {

    const val EVENTS = "events"

    const val CLUB_ID = "club_id"
    const val NAME = "name"
    const val DATE = "date"
    const val DATE_TIME = "date_time"

    fun Access.addEvent(
        clubId: UUID,
        name: String,
        date: LocalDate,
        time: LocalTime?,
    ): Event {
        return insertReturningOne(EVENTS) {
            values(
                CLUB_ID to uuid(clubId),
                NAME to text(name),
                DATE to date(date),
                DATE_TIME to timestamp(time?.let { date.atTime(it) }?.toInstant(UTC)),
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
            dateTime = r.instantOrNull(DATE_TIME),
        )
    }
}
