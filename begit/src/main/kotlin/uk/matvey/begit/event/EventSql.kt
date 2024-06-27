package uk.matvey.begit.event

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import uk.matvey.slon.Access
import uk.matvey.slon.InsertBuilder.Companion.insertInto
import uk.matvey.slon.RecordReader
import uk.matvey.slon.param.DateParam.Companion.date
import uk.matvey.slon.param.JsonbParam.Companion.jsonb
import uk.matvey.slon.param.PlainParam.Companion.genRandomUuid
import uk.matvey.slon.param.PlainParam.Companion.now
import uk.matvey.slon.param.TextParam.Companion.text
import uk.matvey.slon.param.TimestampParam.Companion.timestamp
import uk.matvey.slon.param.UuidParam.Companion.uuid
import uk.matvey.slon.query.update.UpdateQuery.Builder.Companion.update
import java.time.LocalDate
import java.util.UUID

object EventSql {

    const val EVENTS = "begit.events"

    const val ID = "id"
    const val CLUB_ID = "club_id"
    const val ORGANIZED_BY = "organized_by"
    const val TITLE = "title"
    const val DESCRIPTION = "description"
    const val TYPE = "type"
    const val DATE = "date"
    const val DATE_TIME = "date_time"
    const val REFS = "refs"
    const val CREATED_AT = "created_at"
    const val UPDATED_AT = "updated_at"

    fun Access.insertEvent(clubId: UUID, organizedBy: UUID): Event {
        return execute(
            insertInto(EVENTS)
                .set(
                    ID to genRandomUuid(),
                    CLUB_ID to uuid(clubId),
                    ORGANIZED_BY to uuid(organizedBy),
                    TITLE to text("[New event]"),
                    TYPE to text(Event.Type.RUN.name),
                    CREATED_AT to now(),
                    UPDATED_AT to now(),
                )
                .returningOne { r -> r.readEvent() }
        )
    }

    fun Access.updateEvent(event: Event) {
        execute(
            update(EVENTS)
                .set(
                    listOf(
                        DATE to date(event.date),
                        DATE_TIME to timestamp(event.dateTime),
                        TITLE to text(event.title),
                        DESCRIPTION to text(event.description),
                        REFS to jsonb(Json.encodeToString(event.refs)),
                    )
                )
                .where("$ID = ? and $UPDATED_AT = ?", uuid(event.id), timestamp(event.updatedAt))
                .optimistic()
        )
    }

    fun Access.getEventById(id: UUID): Event {
        return queryOne("select * from $EVENTS where $ID = ?", listOf(uuid(id))) { r -> r.readEvent() }
    }

    fun RecordReader.readEvent(): Event {
        return Event(
            id = uuid(ID),
            clubId = uuid(CLUB_ID),
            organizedBy = uuid(ORGANIZED_BY),
            title = string(TITLE),
            description = nullableString(DESCRIPTION),
            type = Event.Type.valueOf(string(TYPE)),
            date = nullableString(DATE)?.let(LocalDate::parse),
            dateTime = nullableInstant(DATE_TIME),
            refs = Json.decodeFromString(string(REFS)),
            createdAt = instant(CREATED_AT),
            updatedAt = nullableInstant(UPDATED_AT),
        )
    }
}