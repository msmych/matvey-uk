package uk.matvey.begit.club

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import uk.matvey.begit.club.ClubSql.CLUBS
import uk.matvey.begit.club.ClubSql.CLUB_ID
import uk.matvey.begit.club.ClubSql.CLUB_MEMBERS
import uk.matvey.begit.club.ClubSql.ID
import uk.matvey.begit.club.ClubSql.MEMBER_ID
import uk.matvey.begit.club.ClubSql.REFS
import uk.matvey.begit.club.ClubSql.countMembers
import uk.matvey.begit.club.ClubSql.readClub
import uk.matvey.slon.Repo
import uk.matvey.slon.param.UuidParam.Companion.uuid
import java.util.UUID

class ClubRepo(
    private val repo: Repo,
) {

    fun getById(id: UUID): Club {
        return repo.queryOne("select * from $CLUBS where $ID = ?", listOf(uuid(id))) { r -> r.readClub() }
    }

    fun countClubMembers(clubId: UUID): Int {
        return repo.access { a -> a.countMembers(clubId) }
    }

    fun findClubMemberRefs(clubId: UUID, memberId: UUID): JsonObject? {
        return repo.queryOneNullable(
            "select $REFS from $CLUB_MEMBERS where $CLUB_ID = ? and $MEMBER_ID = ?",
            listOf(uuid(clubId), uuid(memberId))
        ) { Json.parseToJsonElement(it.string(1)).jsonObject }
    }
}