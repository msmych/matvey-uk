package uk.matvey.corsa.club

import uk.matvey.corsa.CorsaSql.ID
import uk.matvey.corsa.CorsaSql.UPDATED_AT
import uk.matvey.slon.RecordReader
import uk.matvey.slon.access.Access
import uk.matvey.slon.access.AccessKit.deleteFrom
import uk.matvey.slon.access.AccessKit.insertInto
import uk.matvey.slon.access.AccessKit.insertReturningOne
import uk.matvey.slon.param.PlainParam.Companion.now
import uk.matvey.slon.param.TextParam.Companion.text
import uk.matvey.slon.param.UuidParam.Companion.uuid
import java.util.UUID

object ClubSql {

    const val CLUBS = "clubs"

    const val NAME = "name"

    const val CLUBS_ATHLETES = "clubs_athletes"

    const val CLUB_ID = "club_id"
    const val ATHLETE_ID = "athlete_id"
    const val ROLE = "role"

    fun Access.addClub(name: String, athleteId: UUID): Club {
        val club = insertReturningOne(CLUBS) {
            values(
                NAME to text(name),
                UPDATED_AT to now(),
            )
            returning(::readClub)
        }
        insertInto(CLUBS_ATHLETES) {
            values(
                CLUB_ID to uuid(club.id),
                ATHLETE_ID to uuid(athleteId),
                ROLE to text("FOUNDER"),
            )
        }
        return club
    }

    fun Access.removeClub(id: UUID) {
        deleteFrom(CLUBS, "$ID = ?", uuid(id))
    }

    fun Access.getClub(id: UUID): Club {
        return queryOne("select * from $CLUBS where $ID = ?", listOf(uuid(id))) { readClub(it) }
    }

    fun readClub(r: RecordReader) = Club(
        r.uuid(ID),
        r.string(NAME)
    )
}
