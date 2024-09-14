package uk.matvey.corsa.club

import uk.matvey.corsa.CorsaSql.ID
import uk.matvey.corsa.CorsaSql.UPDATED_AT
import uk.matvey.slon.RecordReader
import uk.matvey.slon.access.Access
import uk.matvey.slon.query.DeleteQueryBuilder.Companion.deleteFrom
import uk.matvey.slon.query.InsertOneQueryBuilder.Companion.insertOneInto
import uk.matvey.slon.query.Query.Companion.plainQuery
import uk.matvey.slon.query.ReturningQuery.Companion.returning
import uk.matvey.slon.value.Pg
import uk.matvey.slon.value.PgUuid.Companion.toPgUuid
import java.util.UUID

object ClubSql {

    const val CLUBS = "clubs"

    const val NAME = "name"

    const val CLUBS_ATHLETES = "clubs_athletes"

    const val CLUB_ID = "club_id"
    const val ATHLETE_ID = "athlete_id"
    const val ROLE = "role"

    fun Access.addClub(name: String, athleteId: UUID): Club {
        val club = query(
            insertOneInto(CLUBS) {
                set(NAME, name)
                set(UPDATED_AT, Pg.now())
            }
                .returning {
                    readClub(it)
                }
        ).single()
        execute(insertOneInto(CLUBS_ATHLETES) {
            set(CLUB_ID, club.id)
            set(ATHLETE_ID, athleteId)
            set(ROLE, "FOUNDER")
        })
        return club
    }

    fun Access.removeClub(id: UUID) {
        execute(
            deleteFrom(CLUBS).where("$ID = ?", id.toPgUuid())
        )
    }

    fun Access.getClub(id: UUID): Club {
        return query(
            plainQuery(
                "select * from $CLUBS where $ID = ?",
                listOf(id.toPgUuid())
            ) { readClub(it) }
        ).single()
    }

    fun readClub(r: RecordReader) = Club(
        r.uuid(ID),
        r.string(NAME)
    )
}
