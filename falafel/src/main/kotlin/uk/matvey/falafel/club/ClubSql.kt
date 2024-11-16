package uk.matvey.falafel.club

import uk.matvey.app.MatveySql.CREATED_AT
import uk.matvey.app.MatveySql.ID
import uk.matvey.app.MatveySql.UPDATED_AT
import uk.matvey.falafel.FalafelSql.FALAFEL
import uk.matvey.kit.json.JsonKit.jsonDeserialize
import uk.matvey.slon.RecordReader
import uk.matvey.slon.access.Access
import uk.matvey.slon.access.AccessKit.queryOne
import uk.matvey.slon.value.PgUuid.Companion.toPgUuid
import java.util.UUID

object ClubSql {

    const val CLUBS = "$FALAFEL.clubs"
    const val NAME = "name"
    const val REFS = "refs"

    fun Access.getClubById(id: UUID): Club {
        return queryOne(
            """select * from $CLUBS where $ID = ?""",
            listOf(id.toPgUuid()),
            ::readClub
        )
    }

    fun readClub(reader: RecordReader): Club {
        return Club(
            reader.uuid(ID),
            reader.string(NAME),
            jsonDeserialize(reader.string(REFS)),
            reader.instant(CREATED_AT),
            reader.instant(UPDATED_AT),
        )
    }
}
