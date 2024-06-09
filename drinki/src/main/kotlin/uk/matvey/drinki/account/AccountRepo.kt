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
import uk.matvey.slon.command.Insert.Builder.Companion.insert
import uk.matvey.slon.command.Update.Builder.Companion.update

class AccountRepo(private val repo: Repo) {

    fun add(account: Account) {
        repo.execute(
            insert(ACCOUNTS)
                .values(
                    ID to uuid(account.id),
                    TG_SESSION to jsonb(JSON.encodeToString(account.tgSession)),
                    CREATED_AT to timestamp(account.createdAt),
                    UPDATED_AT to timestamp(account.updatedAt),
                )
        )
    }

    fun update(account: Account) {
        repo.execute(
            update(ACCOUNTS)
                .set(TG_SESSION to jsonb(JSON.encodeToString(account.tgSession)))
                .where("$ID = ?", uuid(account.id))
        )
    }

    fun findByTgUserId(tgUserId: Long): Account? {
        return repo.query(
            "SELECT * FROM $ACCOUNTS WHERE $TG_SESSION ->> 'userId' = ?",
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
