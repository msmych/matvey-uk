package uk.matvey.corsa.athlete

import uk.matvey.kit.json.JsonKit
import uk.matvey.kit.json.JsonKit.jsonSerialize
import uk.matvey.slon.RecordReader
import uk.matvey.slon.access.Access
import uk.matvey.slon.access.AccessKit.insertInto
import uk.matvey.slon.param.IntParam.Companion.int
import uk.matvey.slon.param.JsonbParam.Companion.jsonb
import uk.matvey.slon.param.PlainParam.Companion.now
import uk.matvey.slon.param.TextParam.Companion.text

object AthleteSql {

    const val ATHLETES = "athletes"

    const val ID = "id"
    const val NAME = "name"
    const val REFS = "refs"
    const val TG = "($REFS -> 'tg')::bigint"
    const val UPDATED_AT = "updated_at"

    fun Access.ensureAthlete(tg: Long, name: String): Athlete {
        insertInto(ATHLETES) {
            values(
                NAME to text(name),
                REFS to jsonb(jsonSerialize(Athlete.Refs(tg))),
                UPDATED_AT to now(),
            )
            onConflictDoNothing()
        }
        return queryOne(
            "select * from $ATHLETES where $TG = ?",
            listOf(int(tg)),
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
