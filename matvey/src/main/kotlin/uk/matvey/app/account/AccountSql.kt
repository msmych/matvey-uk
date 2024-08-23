package uk.matvey.app.account

import uk.matvey.kit.json.JsonKit.jsonDeserialize
import uk.matvey.kit.json.JsonKit.jsonSerialize
import uk.matvey.slon.RecordReader
import uk.matvey.slon.access.Access
import uk.matvey.slon.access.AccessKit.insertReturningOne
import uk.matvey.slon.access.AccessKit.update
import uk.matvey.slon.param.IntParam.Companion.int
import uk.matvey.slon.param.JsonbParam.Companion.jsonb
import uk.matvey.slon.param.PlainParam.Companion.now
import uk.matvey.slon.param.TextParam.Companion.text
import uk.matvey.slon.param.UuidParam.Companion.uuid
import java.util.UUID

object AccountSql {

    const val ACCOUNTS = "accounts"

    const val STATE = "state"
    const val NAME = "name"
    const val TAGS = "tags"
    const val REFS = "refs"

    const val TG = "($REFS ->> 'tg')::bigint"

    fun Access.ensureAccount(name: String, tgUserId: Long): Account {
        return queryOneOrNull(
            "select * from $ACCOUNTS where $TG = ?",
            listOf(int(tgUserId)),
            ::readAccount
        )
            ?: insertReturningOne(ACCOUNTS) {
                values(
                    NAME to text(name),
                    STATE to text(Account.State.PENDING.name),
                    REFS to jsonb(jsonSerialize(Account.Refs(tgUserId))),
                    "updated_at" to now(),
                )
                onConflictDoNothing()
                returning(::readAccount)
            }
    }

    fun Access.getAccountByTgUserId(tgUserId: Long): Account {
        return queryOne(
            "select * from $ACCOUNTS where $TG = ?",
            listOf(int(tgUserId)),
            ::readAccount
        )
    }

    fun Access.updateAccountStatus(id: UUID, state: Account.State) {
        update(ACCOUNTS) {
            set(STATE to text(state.name))
            where("id = ?", uuid(id))
        }
    }

    fun readAccount(r: RecordReader): Account {
        return Account(
            id = r.uuid("id"),
            state = Account.State.valueOf(r.string(STATE)),
            tags = r.stringList(TAGS).map(Account.Tag::valueOf).toSet(),
            name = r.string(NAME),
            refs = jsonDeserialize(r.string(REFS)),
        )
    }
}
