package uk.matvey.corsa.club

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import uk.matvey.corsa.club.ClubSql.getClub
import uk.matvey.corsa.event.EventSql.getEventsByClubId
import uk.matvey.slon.repo.Repo
import java.util.UUID

class ClubService(
    private val repo: Repo,
) {

    suspend fun getClubDetails(clubId: UUID): ClubDetails = coroutineScope {
        val club = async { repo.access { a -> a.getClub(clubId) } }
        val events = async { repo.access { a -> a.getEventsByClubId(clubId) } }
        ClubDetails(club.await(), events.await())
    }
}