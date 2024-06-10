package uk.matvey.begit.member

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import uk.matvey.slon.Access
import uk.matvey.slon.InsertBuilder.Companion.insertInto
import uk.matvey.slon.RecordReader
import uk.matvey.slon.param.JsonbParam.Companion.jsonb
import uk.matvey.slon.param.PlainParam.Companion.genRandomUuid
import uk.matvey.slon.param.PlainParam.Companion.now
import uk.matvey.slon.param.TextParam.Companion.text

object MemberSql {

    private const val MEMBERS = "begit.members"

    private const val ID = "id"
    private const val NAME = "name"
    private const val REFS = "refs"
    private const val CREATED_AT = "created_at"
    private const val UPDATED_AT = "updated_at"

    private const val TG_ID = "($REFS ->> 'tgId')"

    fun Access.addMember(chatId: Long, name: String): Member {
        return execute(
            insertInto(MEMBERS)
                .set(
                    ID to genRandomUuid(),
                    NAME to text(name),
                    REFS to jsonb(Json.encodeToString(Member.Refs(chatId))),
                    CREATED_AT to now(),
                    UPDATED_AT to now(),
                )
                .onConflict("($TG_ID) do update set $NAME = '$name'")
                .returning { r -> r.readMember() }
        ).single()
    }

    fun RecordReader.readMember(): Member {
        return Member(
            id = uuid(ID),
            name = string(NAME),
            refs = Json.decodeFromString(string(REFS)),
            createdAt = instant(CREATED_AT),
            updatedAt = instant(UPDATED_AT),
        )
    }
}