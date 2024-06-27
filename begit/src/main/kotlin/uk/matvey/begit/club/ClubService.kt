package uk.matvey.begit.club

import kotlinx.serialization.json.JsonObject
import uk.matvey.begit.club.ClubSql.CLUB_ID
import uk.matvey.begit.club.ClubSql.CLUB_MEMBERS
import uk.matvey.begit.club.ClubSql.MEMBER_ID
import uk.matvey.begit.club.ClubSql.addClubMember
import uk.matvey.begit.club.ClubSql.countMembers
import uk.matvey.begit.club.ClubSql.ensureClub
import uk.matvey.begit.member.MemberSql.ensureMember
import uk.matvey.slon.Repo
import uk.matvey.slon.param.UuidParam.Companion.uuid
import uk.matvey.slon.query.update.DeleteQuery.Builder.Companion.deleteFrom
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

    fun addClubMember(clubId: UUID, tgUserId: Long, username: String, refs: JsonObject): Boolean {
        return repo.access { a ->
            val member = a.ensureMember(tgUserId, username)
            a.addClubMember(clubId, member.id, refs)
        }
    }

    fun removeClubMember(clubId: UUID, memberId: UUID): Boolean {
        return repo.execute (
            deleteFrom(CLUB_MEMBERS)
                .where("$CLUB_ID = ? and $MEMBER_ID = ?", uuid(clubId), uuid(memberId))
        ) > 0
    }
}