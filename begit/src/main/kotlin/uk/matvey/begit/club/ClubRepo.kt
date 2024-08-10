package uk.matvey.begit.club

import kotlinx.serialization.json.JsonObject
import uk.matvey.begit.club.ClubMemberSql.countClubMembers
import uk.matvey.begit.club.ClubMemberSql.findAllClubsByAthleteId
import uk.matvey.begit.club.ClubMemberSql.findClubMemberRefs
import uk.matvey.begit.club.ClubSql.getClubById
import uk.matvey.slon.repo.Repo
import java.util.UUID

class ClubRepo(
    private val repo: Repo,
) {

    suspend fun getById(id: UUID): Club {
        return repo.access { a -> a.getClubById(id) }
    }

    suspend fun countClubMembers(clubId: UUID): Int {
        return repo.access { a -> a.countClubMembers(clubId) }
    }

    suspend fun findClubMemberRefs(clubId: UUID, memberId: UUID): JsonObject? {
        return repo.access { a -> a.findClubMemberRefs(clubId, memberId) }
    }

    suspend fun findAllByAthleteId(athleteId: UUID): List<Club> {
        return repo.access { a -> a.findAllClubsByAthleteId(athleteId) }
    }
}