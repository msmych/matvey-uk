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

    const val MEMBERS = "begit.members"

    const val ID = "id"
    const val NAME = "name"
    const val REFS = "refs"
    const val CREATED_AT = "created_at"
    const val UPDATED_AT = "updated_at"
    const val TG_ID = "($REFS ->> 'tgId')"

    fun Access.ensureMember(tgId: Long, name: String): Member {
        return execute(
            insertInto(MEMBERS)
                .set(
                    ID to genRandomUuid(),
                    NAME to text(name),
                    REFS to jsonb(Json.encodeToString(Member.Refs(tgId))),
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