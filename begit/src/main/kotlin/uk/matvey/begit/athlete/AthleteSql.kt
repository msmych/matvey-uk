package uk.matvey.begit.athlete

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import uk.matvey.slon.Access
import uk.matvey.slon.InsertBuilder.Companion.insertInto
import uk.matvey.slon.RecordReader
import uk.matvey.slon.param.JsonbParam.Companion.jsonb
import uk.matvey.slon.param.PlainParam.Companion.genRandomUuid
import uk.matvey.slon.param.PlainParam.Companion.now
import uk.matvey.slon.param.TextParam.Companion.text

object AthleteSql {

    const val ATHLETES = "begit.athletes"

    const val ID = "id"
    const val NAME = "name"
    const val REFS = "refs"
    const val CREATED_AT = "created_at"
    const val UPDATED_AT = "updated_at"
    const val TG_CHAT_ID = "($REFS ->> 'tgChatId')"

    fun Access.ensureAthlete(tgChatId: Long, name: String): Athlete {
        return execute(
            insertInto(ATHLETES)
                .set(
                    ID to genRandomUuid(),
                    NAME to text(name),
                    REFS to jsonb(Json.encodeToString(Athlete.Refs(tgChatId))),
                    CREATED_AT to now(),
                    UPDATED_AT to now(),
                )
                .onConflict("($TG_CHAT_ID) do update set $NAME = '$name'")
                .returningOne { r -> r.readAthlete() }
        )
    }

    fun Access.getAthleteByTgChatId(tgChatId: Long): Athlete {
        return queryOne(
            "select * from $ATHLETES where $TG_CHAT_ID = ?",
            listOf(text(tgChatId.toString()))
        ) { it.readAthlete() }
    }

    fun RecordReader.readAthlete(): Athlete {
        return Athlete(
            id = uuid(ID),
            name = string(NAME),
            refs = Json.decodeFromString(string(REFS)),
            createdAt = instant(CREATED_AT),
            updatedAt = instant(UPDATED_AT),
        )
    }
}