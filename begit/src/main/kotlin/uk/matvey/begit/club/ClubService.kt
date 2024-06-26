package uk.matvey.begit.club

import uk.matvey.begit.club.ClubSql.addClubMember
import uk.matvey.begit.club.ClubSql.countMembers
import uk.matvey.begit.club.ClubSql.ensureClub
import uk.matvey.begit.member.MemberSql.ensureMember
import uk.matvey.slon.Repo
import java.util.UUID

class ClubService(
    private val repo: Repo,
) {

    fun ensureClub(name: String, tgId: Long): Pair<Club, Int> {
        return repo.access { a ->
            val club = a.ensureClub(name, tgId)
            club to a.countMembers(club.id)
        }
    }

    fun addClubMember(clubId: UUID, userId: Long, username: String): Boolean {
        return repo.access { a ->
            val member = a.ensureMember(userId, username)
            a.addClubMember(clubId, member.id)
        }
    }
}