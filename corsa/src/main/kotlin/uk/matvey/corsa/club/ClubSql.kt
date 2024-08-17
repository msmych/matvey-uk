package uk.matvey.corsa.club

import uk.matvey.slon.RecordReader
import uk.matvey.slon.access.Access
import uk.matvey.slon.access.AccessKit.deleteFrom
import uk.matvey.slon.access.AccessKit.insertReturning
import uk.matvey.slon.param.PlainParam.Companion.now
import uk.matvey.slon.param.TextParam.Companion.text
import uk.matvey.slon.param.UuidParam.Companion.uuid
import java.util.UUID

object ClubSql {

    const val CLUBS = "clubs"

    const val ID = "id"
    const val NAME = "name"
    const val UPDATED_AT = "updated_at"

    fun Access.addClub(name: String): Club {
        return insertReturning(CLUBS) {
            values(
                NAME to text(name),
                UPDATED_AT to now(),
            )
            returning(::readClub)
        }.single()
    }

    fun Access.removeClub(id: UUID) {
        deleteFrom(CLUBS, "$ID = ?", uuid(id))
    }

    fun readClub(r: RecordReader) = Club(
        r.uuid(ID),
        r.string(NAME)
    )
}