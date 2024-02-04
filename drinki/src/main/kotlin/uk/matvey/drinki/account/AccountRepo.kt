package uk.matvey.drinki.account

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import uk.matvey.drinki.Setup.JSON
import uk.matvey.drinki.account.AccountSql.ACCOUNTS
import uk.matvey.drinki.account.AccountSql.CREATED_AT
import uk.matvey.drinki.account.AccountSql.ID
import uk.matvey.drinki.account.AccountSql.TG_SESSION
import uk.matvey.drinki.account.AccountSql.UPDATED_AT
import uk.matvey.postal.QueryParam.*
import uk.matvey.postal.QueryParams
import uk.matvey.postal.Repo

class AccountRepo(private val repo: Repo) {

    fun add(account: Account) {
        repo.insert(
            ACCOUNTS,
            QueryParams()
                .add(ID, UuidParam(account.id))
                .add(TG_SESSION, JsonbParam(JSON.encodeToString(account.tgSession)))
                .add(CREATED_AT, TimestampParam(account.createdAt))
                .add(UPDATED_AT, TimestampParam(account.updatedAt))
        )
    }

    fun update(account: Account) {
        repo.update(
            ACCOUNTS,
            QueryParams()
                .add(TG_SESSION, JsonbParam(JSON.encodeToString(account.tgSession))),
            "$ID = ?",
            QueryParams()
                .add(ID, UuidParam(account.id))
        )
    }

    fun findByTgUserId(tgUserId: Long): Account? {
        return repo.select(
            "select * from $ACCOUNTS where $TG_SESSION ->> 'userId' = ?",
            QueryParams().add("userId", TextParam(tgUserId.toString()))
        ) { ex ->
            Account(
                ex.uuid(ID),
                JSON.parseToJsonElement(ex.jsonb(TG_SESSION))
                    .takeIf { it.jsonObject.containsKey("userId") }
                    ?.let { JSON.decodeFromJsonElement(it) },
                ex.instant(CREATED_AT),
                ex.instant(UPDATED_AT)
            )
        }
            .singleOrNull()
    }

    fun getByTgUserId(tgUserId: Long): Account = requireNotNull(findByTgUserId(tgUserId))
}