package uk.matvey.begit.club

import kotlinx.serialization.json.JsonObject
import uk.matvey.begit.athlete.AthleteSql.ensureAthlete
import uk.matvey.begit.club.ClubMemberSql.ATHLETE_ID
import uk.matvey.begit.club.ClubMemberSql.CLUB_MEMBERS
import uk.matvey.begit.club.ClubMemberSql.addClubMember
import uk.matvey.begit.club.ClubMemberSql.countClubMembers
import uk.matvey.begit.club.ClubSql.ensureClub
import uk.matvey.begit.club.ClubSql.updateClub
import uk.matvey.begit.event.EventParticipantSql.EVENT_ID
import uk.matvey.begit.event.EventParticipantSql.EVENT_PARTICIPANTS
import uk.matvey.begit.event.EventSql
import uk.matvey.begit.event.EventSql.EVENTS
import uk.matvey.slon.Repo
import uk.matvey.slon.param.UuidParam.Companion.uuid
import uk.matvey.slon.query.update.DeleteQuery.Builder.Companion.deleteFrom
import java.util.UUID

class ClubService(
    private val repo: Repo,
) {

    fun ensureClub(name: String, tgChatId: Long): Pair<Club, Int> {
        return repo.access { a ->
            val club = a.ensureClub(name, tgChatId)
            club to a.countClubMembers(club.id)
        }
    }

    fun updateClub(club: Club) {
        repo.access { a -> a.updateClub(club) }
    }

    fun addClubMember(clubId: UUID, tgUserId: Long, username: String, refs: JsonObject): Boolean {
        return repo.access { a ->
            val athlete = a.ensureAthlete(tgUserId, username)
            a.addClubMember(clubId, athlete.id, refs)
        }
    }

    fun removeClubMember(clubId: UUID, athleteId: UUID): Boolean {
        return repo.access { a ->
            a.execute(
                deleteFrom(EVENT_PARTICIPANTS)
                    .where(
                        """$ATHLETE_ID = ?
                        | and $EVENT_ID in (
                        |  select $EVENT_ID from $EVENTS where ${EventSql.CLUB_ID} = ?
                        | )""".trimMargin(),
                        uuid(athleteId),
                        uuid(clubId),
                    )
            )
            a.execute(
                deleteFrom(CLUB_MEMBERS)
                    .where("${EventSql.CLUB_ID} = ? and $ATHLETE_ID = ?", uuid(clubId), uuid(athleteId))
            )
        } > 0
    }
}