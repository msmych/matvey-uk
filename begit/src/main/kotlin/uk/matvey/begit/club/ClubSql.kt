package uk.matvey.begit.club

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import uk.matvey.slon.Access
import uk.matvey.slon.InsertBuilder.Companion.insertInto
import uk.matvey.slon.RecordReader
import uk.matvey.slon.param.JsonbParam.Companion.jsonb
import uk.matvey.slon.param.PlainParam.Companion.genRandomUuid
import uk.matvey.slon.param.PlainParam.Companion.now
import uk.matvey.slon.param.TextParam.Companion.text
import uk.matvey.slon.param.UuidParam.Companion.uuid
import java.util.UUID

object ClubSql {

    const val CLUBS = "begit.clubs"
    const val ID = "id"
    const val NAME = "name"
    const val DESCRIPTION = "description"
    const val REFS = "refs"
    const val CREATED_AT = "created_at"
    const val UPDATED_AT = "updated_at"
    const val TG_ID = "($REFS ->> 'tgId')"

    const val CLUB_MEMBERS = "begit.club_members"
    const val CLUB_ID = "club_id"
    const val MEMBER_ID = "member_id"

    fun Access.ensureClub(name: String, tgId: Long): Club {
        return execute(insertInto(CLUBS)
            .set(
                ID to genRandomUuid(),
                NAME to text(name),
                REFS to jsonb(Json.encodeToString(Club.Refs(tgId))),
                CREATED_AT to now(),
                UPDATED_AT to now(),
            )
            .onConflict("($TG_ID) do update set $NAME = '$name'")
            .returning { r -> r.readClub() }
        ).single()
    }

    fun Access.countMembers(clubId: UUID): Int {
        return queryOne(
            "select count(*) from $CLUB_MEMBERS where $CLUB_ID = ?",
            listOf(uuid(clubId))
        ) { it.int(1) }
    }

    fun Access.addClubMember(clubId: UUID, memberId: UUID, refs: JsonObject): Boolean {
        return execute(
            insertInto(CLUB_MEMBERS)
                .set(CLUB_ID to uuid(clubId), MEMBER_ID to uuid(memberId), REFS to jsonb(Json.encodeToString(refs)))
                .onConflictDoNothing()
                .build()
        ) > 0
    }

    fun RecordReader.readClub(): Club {
        return Club(
            id = uuid(ID),
            name = string(NAME),
            description = nullableString(DESCRIPTION),
            refs = Json.decodeFromString(string(REFS)),
            createdAt = instant(CREATED_AT),
            updatedAt = instant(UPDATED_AT),
        )
    }
}