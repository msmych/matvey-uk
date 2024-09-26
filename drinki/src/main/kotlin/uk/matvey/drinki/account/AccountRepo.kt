package uk.matvey.drinki.account

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import uk.matvey.drinki.account.AccountSql.ACCOUNTS
import uk.matvey.drinki.account.AccountSql.CREATED_AT
import uk.matvey.drinki.account.AccountSql.ID
import uk.matvey.drinki.account.AccountSql.TG_SESSION
import uk.matvey.drinki.account.AccountSql.UPDATED_AT
import uk.matvey.kit.json.JsonKit.JSON
import uk.matvey.slon.repo.Repo
import uk.matvey.slon.repo.RepoKit.insertOneInto
import uk.matvey.slon.repo.RepoKit.queryOneOrNull
import uk.matvey.slon.repo.RepoKit.update
import uk.matvey.slon.value.PgText.Companion.toPgText
import uk.matvey.slon.value.PgUuid.Companion.toPgUuid

class AccountRepo(private val repo: Repo) {

    fun add(account: Account) {
        repo.insertOneInto(ACCOUNTS) {
            set(ID, account.id)
            set(TG_SESSION, JSON.encodeToString(account.tgSession))
            set(CREATED_AT, account.createdAt)
            set(UPDATED_AT, account.updatedAt)
        }
    }

    fun update(account: Account) {
        repo.update(ACCOUNTS) {
            set(TG_SESSION, JSON.encodeToString(account.tgSession))
            where("$ID = ?", account.id.toPgUuid())
        }
    }

    fun findByTgUserId(tgUserId: Long): Account? {
        return repo.queryOneOrNull(
            "select * from $ACCOUNTS where $TG_SESSION ->> 'userId' = ?",
            listOf(tgUserId.toString().toPgText())
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
    }

    fun getByTgUserId(tgUserId: Long): Account = requireNotNull(findByTgUserId(tgUserId))
}
