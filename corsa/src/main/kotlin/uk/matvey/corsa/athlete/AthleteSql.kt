package uk.matvey.corsa.athlete

import uk.matvey.corsa.CorsaSql.ID
import uk.matvey.corsa.CorsaSql.UPDATED_AT
import uk.matvey.kit.json.JsonKit
import uk.matvey.kit.json.JsonKit.jsonSerialize
import uk.matvey.slon.RecordReader
import uk.matvey.slon.access.Access
import uk.matvey.slon.access.AccessKit.queryOneOrNull
import uk.matvey.slon.query.InsertOneQueryBuilder.Companion.insertOneInto
import uk.matvey.slon.query.OnConflictClause
import uk.matvey.slon.query.Query.Companion.plainQuery
import uk.matvey.slon.value.Pg
import uk.matvey.slon.value.PgInt.Companion.toPgInt
import uk.matvey.slon.value.PgUuid.Companion.toPgUuid
import java.util.UUID

object AthleteSql {

    const val ATHLETES = "athletes"

    const val NAME = "name"
    const val REFS = "refs"
    const val TG = "($REFS -> 'tg')::bigint"

    fun Access.ensureAthlete(tg: Long, name: String): Athlete {
        return queryOneOrNull(
            "select * from $ATHLETES where $TG = ?",
            listOf(tg.toPgInt()),
            ::readAthlete,
        ) ?: query(insertOneInto(ATHLETES)
            .set(NAME, name)
            .set(REFS, jsonSerialize(Athlete.Refs(tg)))
            .set(UPDATED_AT, Pg.now())
            .onConflict(OnConflictClause.doNothing())
            .returning { readAthlete(it) }
        ).single()
    }

    fun Access.getAthlete(id: UUID): Athlete {
        return query(
            plainQuery(
                "select * from $ATHLETES where $ID = ?",
                listOf(id.toPgUuid()),
                ::readAthlete,
            )
        ).single()
    }

    fun readAthlete(reader: RecordReader): Athlete {
        return Athlete(
            id = reader.uuid(ID),
            name = reader.string(NAME),
            refs = JsonKit.jsonDeserialize(reader.string(REFS)),
        )
    }
}
