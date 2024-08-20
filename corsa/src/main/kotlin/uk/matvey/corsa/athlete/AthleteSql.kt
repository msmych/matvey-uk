package uk.matvey.corsa.athlete

import uk.matvey.corsa.CorsaSql.ID
import uk.matvey.corsa.CorsaSql.UPDATED_AT
import uk.matvey.kit.json.JsonKit
import uk.matvey.kit.json.JsonKit.jsonSerialize
import uk.matvey.slon.RecordReader
import uk.matvey.slon.access.Access
import uk.matvey.slon.access.AccessKit.insertReturningOne
import uk.matvey.slon.param.IntParam.Companion.int
import uk.matvey.slon.param.JsonbParam.Companion.jsonb
import uk.matvey.slon.param.PlainParam.Companion.now
import uk.matvey.slon.param.TextParam.Companion.text
import uk.matvey.slon.param.UuidParam.Companion.uuid
import java.util.UUID

object AthleteSql {

    const val ATHLETES = "athletes"

    const val NAME = "name"
    const val REFS = "refs"
    const val TG = "($REFS -> 'tg')::bigint"

    fun Access.ensureAthlete(tg: Long, name: String): Athlete {
        return queryOneOrNull(
            "select * from $ATHLETES where $TG = ?",
            listOf(int(tg)),
            ::readAthlete,
        ) ?: insertReturningOne(ATHLETES) {
            values(
                NAME to text(name),
                REFS to jsonb(jsonSerialize(Athlete.Refs(tg))),
                UPDATED_AT to now(),
            )
            onConflictDoNothing()
            returning { readAthlete(it) }
        }
    }

    fun Access.getAthlete(id: UUID): Athlete {
        return queryOne(
            "select * from $ATHLETES where $ID = ?",
            listOf(uuid(id)),
            ::readAthlete,
        )
    }

    fun readAthlete(reader: RecordReader): Athlete {
        return Athlete(
            id = reader.uuid(ID),
            name = reader.string(NAME),
            refs = JsonKit.jsonDeserialize(reader.string(REFS)),
        )
    }
}
