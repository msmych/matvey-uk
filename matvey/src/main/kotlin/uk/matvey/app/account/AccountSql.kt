package uk.matvey.app.account

import uk.matvey.kit.json.JsonKit.jsonDeserialize
import uk.matvey.kit.json.JsonKit.jsonSerialize
import uk.matvey.slon.RecordReader
import uk.matvey.slon.access.Access
import uk.matvey.slon.query.InsertOneBuilder.Companion.insertOneInto
import uk.matvey.slon.query.OnConflictClause.Companion.doNothing
import uk.matvey.slon.query.Query.Companion.plainQuery
import uk.matvey.slon.query.UpdateQueryBuilder
import uk.matvey.slon.value.Pg
import uk.matvey.slon.value.PgInt.Companion.toPgInt
import uk.matvey.slon.value.PgUuid.Companion.toPgUuid
import java.util.UUID

object AccountSql {

    const val ACCOUNTS = "accounts"

    const val STATE = "state"
    const val NAME = "name"
    const val TAGS = "tags"
    const val REFS = "refs"

    const val TG = "($REFS ->> 'tg')::bigint"

    fun Access.ensureAccount(name: String, tgUserId: Long): Account {
        return query(
            plainQuery(
                "select * from $ACCOUNTS where $TG = ?",
                listOf(tgUserId.toPgInt()),
                ::readAccount
            )
        ).singleOrNull()
            ?: query(insertOneInto(ACCOUNTS)
                .set(NAME, name)
                .set(STATE, Account.State.PENDING)
                .set(REFS, jsonSerialize(Account.Refs(tgUserId)))
                .set("updated_at", Pg.now())
                .onConflict(doNothing())
                .returning {
                    readAccount(it)
                }
            ).single()
    }

    fun Access.getAccountById(id: UUID): Account {
        return query(
            plainQuery(
                "select * from $ACCOUNTS where id = ?",
                listOf(id.toPgUuid()),
                ::readAccount
            )
        ).single()
    }

    fun Access.getAccountByTgUserId(tgUserId: Long): Account {
        return query(
            plainQuery(
                "select * from $ACCOUNTS where $TG = ?",
                listOf(tgUserId.toPgInt()),
                ::readAccount
            )
        ).single()
    }

    fun Access.updateAccountStatus(id: UUID, state: Account.State) {
        execute(
            UpdateQueryBuilder.update(ACCOUNTS)
                .set(STATE, state)
                .where("id = ?", id.toPgUuid())
        )
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
