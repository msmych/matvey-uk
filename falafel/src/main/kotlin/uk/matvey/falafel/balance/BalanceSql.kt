package uk.matvey.falafel.balance

import uk.matvey.app.MatveySql.CREATED_AT
import uk.matvey.app.MatveySql.ID
import uk.matvey.app.MatveySql.UPDATED_AT
import uk.matvey.falafel.FalafelSql.FALAFEL
import uk.matvey.slon.RecordReader
import uk.matvey.slon.access.Access
import uk.matvey.slon.access.AccessKit.queryOneOrNull
import uk.matvey.slon.access.AccessKit.updateSingle
import uk.matvey.slon.query.InsertOneQueryBuilder.Companion.insertOneInto
import uk.matvey.slon.query.OnConflict.Companion.doNothing
import uk.matvey.slon.query.ReturningQuery.Companion.returning
import uk.matvey.slon.query.UpdateQueryBuilder
import uk.matvey.slon.value.Pg
import uk.matvey.slon.value.PgInt.Companion.toPgInt
import uk.matvey.slon.value.PgUuid.Companion.toPgUuid
import java.util.UUID

object BalanceSql {

    const val BALANCES = "$FALAFEL.balances"

    const val ACCOUNT_ID = "account_id"
    const val CURRENT = "current"

    fun Access.ensureBalance(accountId: UUID): Balance {
        val existing = queryOneOrNull(
            "select * from $BALANCES where $ACCOUNT_ID = ?",
            listOf(accountId.toPgUuid()),
            ::readBalance
        )
        return existing
            ?: query(insertOneInto(BALANCES) {
                set(ACCOUNT_ID, accountId)
                set(CURRENT, 32.toPgInt())
                set(UPDATED_AT, Pg.now())
                onConflict(doNothing())
            }.returning { readBalance(it) }).single()
    }

    fun Access.topupBalance(accountId: UUID) {
        updateSingle(UpdateQueryBuilder.update(BALANCES) {
            set(CURRENT, Pg.plain("$CURRENT + 1"))
            where("$ACCOUNT_ID = ?", accountId.toPgUuid())
        })
    }

    fun readBalance(reader: RecordReader): Balance {
        return Balance(
            id = reader.uuid(ID),
            accountId = reader.uuid(ACCOUNT_ID),
            current = reader.int(CURRENT),
            createdAt = reader.instant(CREATED_AT),
            updatedAt = reader.instant(UPDATED_AT),
        )
    }
}
