package uk.matvey.drinki.account

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import uk.matvey.drinki.account.AccountSql.ACCOUNTS
import uk.matvey.drinki.account.AccountSql.CREATED_AT
import uk.matvey.drinki.account.AccountSql.ID
import uk.matvey.drinki.account.AccountSql.TG_SESSION
import uk.matvey.drinki.account.AccountSql.UPDATED_AT
import uk.matvey.dukt.json.JsonSetup.JSON
import uk.matvey.slon.QueryParam.Companion.jsonb
import uk.matvey.slon.QueryParam.Companion.text
import uk.matvey.slon.QueryParam.Companion.timestamp
import uk.matvey.slon.QueryParam.Companion.uuid
import uk.matvey.slon.Repo

class AccountRepo(private val repo: Repo) {
    
    fun add(account: Account) {
        repo.insert(
            ACCOUNTS,
            ID to uuid(account.id),
            TG_SESSION to jsonb(JSON.encodeToString(account.tgSession)),
            CREATED_AT to timestamp(account.createdAt),
            UPDATED_AT to timestamp(account.updatedAt),
        )
    }
    
    fun update(account: Account) {
        repo.update(
            ACCOUNTS,
            listOf(TG_SESSION to jsonb(JSON.encodeToString(account.tgSession))),
            "$ID = ?",
            listOf(uuid(account.id)),
        )
    }
    
    fun findByTgUserId(tgUserId: Long): Account? {
        return repo.select(
            "select * from $ACCOUNTS where $TG_SESSION ->> 'userId' = ?",
            listOf(text(tgUserId.toString()))
        ) { reader ->
            Account(
                reader.uuid(ID),
                JSON.parseToJsonElement(reader.string(TG_SESSION))
                    .takeIf { it.jsonObject.containsKey("userId") }
                    ?.let { JSON.decodeFromJsonElement(it) },
                reader.instant(CREATED_AT),
                reader.instant(UPDATED_AT)
            )
        }
            .singleOrNull()
    }
    
    fun getByTgUserId(tgUserId: Long): Account = requireNotNull(findByTgUserId(tgUserId))
}
