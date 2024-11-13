package uk.matvey.falafel.balance

import uk.matvey.app.MatveySql.CREATED_AT
import uk.matvey.app.MatveySql.ID
import uk.matvey.app.MatveySql.UPDATED_AT
import uk.matvey.falafel.FalafelSql.FALAFEL
import uk.matvey.slon.RecordReader
import uk.matvey.slon.access.Access
import uk.matvey.slon.access.AccessKit.queryOneOrNull
import uk.matvey.slon.query.InsertOneQueryBuilder.Companion.insertOneInto
import uk.matvey.slon.query.OnConflict.Companion.doNothing
import uk.matvey.slon.query.ReturningQuery.Companion.returning
import uk.matvey.slon.query.UpdateQueryBuilder
import uk.matvey.slon.value.Pg
import uk.matvey.slon.value.PgInt.Companion.toPgInt
import uk.matvey.slon.value.PgUuid.Companion.toPgUuid
import java.util.UUID

object AccountSql {

    const val ACCOUNTS = "$FALAFEL.accounts"

    const val BALANCE = "balance"

    fun Access.ensureAccount(id: UUID): Balance {
        val existing = queryOneOrNull(
            "select * from $ACCOUNTS where $ID = ?",
            listOf(id.toPgUuid()),
            ::readBalance
        )
        return existing
            ?: query(insertOneInto(ACCOUNTS) {
                set(ID, id)
                set(BALANCE, 32.toPgInt())
                set(UPDATED_AT, Pg.now())
                onConflict(doNothing())
            }.returning { readBalance(it) }).single()
    }

    fun Access.incrementBalances(): List<Pair<UUID, Int>> {
        return query(UpdateQueryBuilder.update(ACCOUNTS) {
            set(BALANCE, Pg.plain("$BALANCE + 1"))
            where("$BALANCE < 32")
        }.returning { r -> r.uuid(ID) to r.int(BALANCE) })
    }

    fun readBalance(reader: RecordReader): Balance {
        return Balance(
            id = reader.uuid(ID),
            accountId = reader.uuid(ID),
            current = reader.int(BALANCE),
            createdAt = reader.instant(CREATED_AT),
            updatedAt = reader.instant(UPDATED_AT),
        )
    }
}
