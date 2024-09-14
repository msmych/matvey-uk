package uk.matvey.corsa.event

import uk.matvey.corsa.CorsaSql.ID
import uk.matvey.corsa.CorsaSql.UPDATED_AT
import uk.matvey.slon.RecordReader
import uk.matvey.slon.access.Access
import uk.matvey.slon.access.AccessKit.queryAll
import uk.matvey.slon.query.DeleteQueryBuilder.Companion.deleteFrom
import uk.matvey.slon.query.InsertOneQueryBuilder.Companion.insertOneInto
import uk.matvey.slon.query.ReturningQuery.Companion.returning
import uk.matvey.slon.value.Pg
import uk.matvey.slon.value.PgUuid.Companion.toPgUuid
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
        return query(
            insertOneInto(EVENTS) {
                set(CLUB_ID, clubId)
                set(NAME, name)
                set(DATE, date)
                set(DATE_TIME, time?.let { date.atTime(it) }?.toInstant(UTC))
                set(UPDATED_AT, Pg.now())
            }
                .returning { readEvent(it) }
        ).single()
    }

    fun Access.removeEvent(eventId: UUID) {
        execute(deleteFrom(EVENTS).where("$ID = ?", eventId.toPgUuid()))
    }

    fun Access.getEventsByClubId(clubId: UUID): List<Event> {
        return queryAll(
            "select * from $EVENTS where $CLUB_ID = ?",
            listOf(clubId.toPgUuid())
        ) { readEvent(it) }
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
