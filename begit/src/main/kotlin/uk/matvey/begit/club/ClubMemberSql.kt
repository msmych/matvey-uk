package uk.matvey.begit.club

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import uk.matvey.begit.club.ClubSql.CLUBS
import uk.matvey.begit.club.ClubSql.ID
import uk.matvey.begit.club.ClubSql.readClub
import uk.matvey.slon.InsertBuilder.Companion.insertInto
import uk.matvey.slon.access.Access
import uk.matvey.slon.param.JsonbParam.Companion.jsonb
import uk.matvey.slon.param.UuidParam.Companion.uuid
import java.util.UUID

object ClubMemberSql {

    const val CLUB_MEMBERS = "begit.club_members"

    const val CLUB_ID = "club_id"
    const val ATHLETE_ID = "athlete_id"
    const val REFS = "refs"

    fun Access.countClubMembers(clubId: UUID): Int {
        return queryOne(
            "select count(*) from $CLUB_MEMBERS where $CLUB_ID = ?",
            listOf(uuid(clubId))
        ) { it.int(1) }
    }

    fun Access.addClubMember(clubId: UUID, athleteId: UUID, refs: JsonObject): Boolean {
        return execute(
            insertInto(CLUB_MEMBERS)
                .set(CLUB_ID to uuid(clubId), ATHLETE_ID to uuid(athleteId), REFS to jsonb(Json.encodeToString(refs)))
                .onConflictDoNothing()
                .build()
        ) > 0
    }

    fun Access.findClubMemberRefs(clubId: UUID, athleteId: UUID): JsonObject? {
        return queryOneNullable(
            "select $REFS from $CLUB_MEMBERS where $CLUB_ID = ? and $ATHLETE_ID = ?",
            listOf(uuid(clubId), uuid(athleteId))
        ) { Json.parseToJsonElement(it.string(1)).jsonObject }
    }

    fun Access.isClubMember(clubId: UUID, athleteId: UUID): Boolean {
        return queryOne(
            "select count(*) from $CLUB_MEMBERS where $CLUB_ID = ? and $ATHLETE_ID = ?",
            listOf(uuid(clubId), uuid(athleteId))
        ) { it.int(1) } == 1
    }

    fun Access.findAllClubsByAthleteId(athleteId: UUID): List<Club> {
        return query(
            "select c.* from $CLUB_MEMBERS cm join $CLUBS c on cm.$CLUB_ID = c.$ID where cm.$ATHLETE_ID = ?",
            listOf(uuid(athleteId))
        ) { it.readClub() }
    }
}