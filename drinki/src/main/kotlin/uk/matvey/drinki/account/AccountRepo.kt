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
import uk.matvey.slon.access.AccessKit.queryOneOrNull
import uk.matvey.slon.query.InsertOneQueryBuilder.Companion.insertOneInto
import uk.matvey.slon.query.UpdateQueryBuilder.Companion.update
import uk.matvey.slon.repo.Repo
import uk.matvey.slon.value.PgText.Companion.toPgText
import uk.matvey.slon.value.PgUuid.Companion.toPgUuid

class AccountRepo(private val repo: Repo) {

    suspend fun add(account: Account) {
        repo.access { a ->
            a.execute(
                insertOneInto(ACCOUNTS) {
                    set(ID, account.id)
                    set(TG_SESSION, JSON.encodeToString(account.tgSession))
                    set(CREATED_AT, account.createdAt)
                    set(UPDATED_AT, account.updatedAt)
                }
            )
        }
    }

    suspend fun update(account: Account) {
        repo.access { a ->
            a.execute(
                update(ACCOUNTS) {
                    set(TG_SESSION, JSON.encodeToString(account.tgSession))
                    where("$ID = ?", account.id.toPgUuid())
                }
            )
        }
    }

    suspend fun findByTgUserId(tgUserId: Long): Account? {
        return repo.access { a ->
            a.queryOneOrNull(
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
    }

    suspend fun getByTgUserId(tgUserId: Long): Account = requireNotNull(findByTgUserId(tgUserId))
}
