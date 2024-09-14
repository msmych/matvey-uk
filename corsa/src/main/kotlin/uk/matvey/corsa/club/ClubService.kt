package uk.matvey.corsa.club

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import uk.matvey.corsa.CorsaSql.ID
import uk.matvey.corsa.club.ClubSql.ATHLETE_ID
import uk.matvey.corsa.club.ClubSql.CLUBS
import uk.matvey.corsa.club.ClubSql.CLUBS_ATHLETES
import uk.matvey.corsa.club.ClubSql.CLUB_ID
import uk.matvey.corsa.club.ClubSql.NAME
import uk.matvey.corsa.club.ClubSql.ROLE
import uk.matvey.corsa.club.ClubSql.getClub
import uk.matvey.corsa.event.EventSql.getEventsByClubId
import uk.matvey.slon.access.AccessKit.queryAll
import uk.matvey.slon.repo.Repo
import uk.matvey.slon.value.PgUuid.Companion.toPgUuid
import java.util.UUID

class ClubService(
    private val repo: Repo,
) {

    suspend fun getAthleteClubs(athleteId: UUID): List<AthleteClub> {
        return repo.access { a ->
            a.queryAll(
                """
                |select *
                |  from $CLUBS c
                |  join $CLUBS_ATHLETES ca on c.$ID = ca.$CLUB_ID
                |  where ca.$ATHLETE_ID = ?
            """.trimMargin(),
                listOf(athleteId.toPgUuid())
            ) {
                AthleteClub(
                    it.uuid(ID),
                    it.string(NAME),
                    it.string(ROLE),
                )
            }
        }
    }

    suspend fun getClubDetails(clubId: UUID): ClubDetails = coroutineScope {
        val club = async { repo.access { a -> a.getClub(clubId) } }
        val events = async { repo.access { a -> a.getEventsByClubId(clubId) } }
        ClubDetails(club.await(), events.await())
    }
}
