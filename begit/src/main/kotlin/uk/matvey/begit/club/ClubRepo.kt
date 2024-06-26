package uk.matvey.begit.club

import uk.matvey.begit.club.ClubSql.countMembers
import uk.matvey.begit.club.ClubSql.getClubByTgId
import uk.matvey.slon.Repo
import java.util.UUID

class ClubRepo(
    private val repo: Repo,
) {

    fun getClubByTgId(tgId: Long): Club {
        return repo.access { a -> a.getClubByTgId(tgId) }
    }

    fun countClubMembers(clubId: UUID): Int {
        return repo.access { a -> a.countMembers(clubId) }
    }
}