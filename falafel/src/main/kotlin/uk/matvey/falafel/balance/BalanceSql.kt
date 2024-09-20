package uk.matvey.falafel.balance

import uk.matvey.app.MatveySql.CREATED_AT
import uk.matvey.app.MatveySql.ID
import uk.matvey.app.MatveySql.UPDATED_AT
import uk.matvey.falafel.FalafelSql.FALAFEL
import uk.matvey.slon.RecordReader
import uk.matvey.slon.access.Access
import uk.matvey.slon.access.AccessKit.queryOneOrNull
import uk.matvey.slon.query.InsertOneQueryBuilder
import uk.matvey.slon.query.OnConflict
import uk.matvey.slon.query.ReturningQuery.Companion.returning
import uk.matvey.slon.value.PgInt.Companion.toPgInt
import uk.matvey.slon.value.PgUuid.Companion.toPgUuid
import java.util.UUID

object BalanceSql {

    const val BALANCE = "$FALAFEL.balance"

    const val ACCOUNT_ID = "account_id"
    const val QUANTITY = "quantity"

    fun Access.ensureBalance(accountId: UUID): Balance {
        return queryOneOrNull(
            "select * from $BALANCE where $ACCOUNT_ID = ?",
            listOf(accountId.toPgUuid())
        ) { readBalance(it) }
            ?: query(InsertOneQueryBuilder.insertOneInto(BALANCE) {
                set(ACCOUNT_ID, accountId)
                set(QUANTITY, 32.toPgInt())
                onConflict(OnConflict.doNothing())
            }.returning { readBalance(it) }).single()
    }

    fun readBalance(reader: RecordReader): Balance {
        return Balance(
            id = reader.uuid(ID),
            accountId = reader.uuid(ACCOUNT_ID),
            quantity = reader.int(QUANTITY),
            createdAt = reader.instant(CREATED_AT),
            updatedAt = reader.instant(UPDATED_AT),
        )
    }
}
