package uk.matvey.begit.club

import uk.matvey.begit.club.ClubSql.addClubMember
import uk.matvey.begit.club.ClubSql.countMembers
import uk.matvey.begit.club.ClubSql.ensureClub
import uk.matvey.begit.club.ClubSql.getClubByTgId
import uk.matvey.begit.member.MemberSql.addMember
import uk.matvey.slon.Repo

class ClubService(
    private val repo: Repo,
) {

    fun ensureClub(name: String, tgId: Long): Pair<Club, Int> {
        return repo.access { a ->
            val club = a.ensureClub(name, tgId)
            club to a.countMembers(club.id)
        }
    }

    fun addClubMember(chatId: Long, userId: Long, username: String) {
        repo.access { a ->
            val member = a.addMember(userId, username)
            val club = a.getClubByTgId(chatId)
            a.addClubMember(club.id, member.id)
        }
    }
}