package uk.matvey.begit.club

import kotlinx.serialization.encodeToString
import uk.matvey.kit.json.JsonKit.JSON
import uk.matvey.slon.InsertBuilder.Companion.insertInto
import uk.matvey.slon.RecordReader
import uk.matvey.slon.access.Access
import uk.matvey.slon.param.JsonbParam.Companion.jsonb
import uk.matvey.slon.param.PlainParam.Companion.genRandomUuid
import uk.matvey.slon.param.PlainParam.Companion.now
import uk.matvey.slon.param.TextParam.Companion.text
import uk.matvey.slon.param.TimestampParam.Companion.timestamp
import uk.matvey.slon.param.UuidParam.Companion.uuid
import uk.matvey.slon.query.update.UpdateQuery.Builder.Companion.update
import java.util.UUID

object ClubSql {

    const val CLUBS = "begit.clubs"
    const val ID = "id"
    const val NAME = "name"
    const val DESCRIPTION = "description"
    const val REFS = "refs"
    const val CREATED_AT = "created_at"
    const val UPDATED_AT = "updated_at"
    const val TG_CHAT_ID = "($REFS ->> 'tgChatId')"

    fun Access.ensureClub(name: String, tgChatId: Long): Club {
        return execute(insertInto(CLUBS)
            .set(
                ID to genRandomUuid(),
                NAME to text(name),
                REFS to jsonb(JSON.encodeToString(Club.Refs(tgChatId))),
                CREATED_AT to now(),
                UPDATED_AT to now(),
            )
            .onConflict(listOf("($TG_CHAT_ID)"), "update set $NAME = '$name'")
            .returningOne { r -> r.readClub() }
        )
    }

    fun Access.updateClub(club: Club) {
        execute(
            update(CLUBS)
                .set(
                    NAME to text(club.name),
                    DESCRIPTION to text(club.description),
                    REFS to jsonb(JSON.encodeToString(club.refs)),
                    UPDATED_AT to now()
                )
                .where("$ID = ? and $UPDATED_AT = ?", uuid(club.id), timestamp(club.updatedAt))
        )
    }

    fun Access.getClubById(id: UUID): Club {
        return queryOne("select * from $CLUBS where $ID = ?", listOf(uuid(id))) { r -> r.readClub() }
    }

    fun Access.getClubByTgChatId(tgChatId: Long): Club {
        return queryOne(
            "select * from $CLUBS where $TG_CHAT_ID = ?",
            listOf(text(tgChatId.toString()))
        ) { r -> r.readClub() }
    }

    fun RecordReader.readClub(): Club {
        return Club(
            id = uuid(ID),
            name = string(NAME),
            description = nullableString(DESCRIPTION),
            refs = JSON.decodeFromString(string(REFS)),
            createdAt = instant(CREATED_AT),
            updatedAt = instant(UPDATED_AT),
        )
    }
}