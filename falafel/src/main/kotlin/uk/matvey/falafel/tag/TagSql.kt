package uk.matvey.falafel.tag

import mu.KotlinLogging
import uk.matvey.falafel.FalafelSql.FALAFEL
import uk.matvey.falafel.balance.BalanceSql
import uk.matvey.slon.RecordReader
import uk.matvey.slon.access.Access
import uk.matvey.slon.access.AccessKit.insertOneInto
import uk.matvey.slon.access.AccessKit.queryAll
import uk.matvey.slon.access.AccessKit.updateSingle
import uk.matvey.slon.exception.UpdateCountMismatchException
import uk.matvey.slon.query.UpdateQueryBuilder.Companion.update
import uk.matvey.slon.repo.Repo
import uk.matvey.slon.repo.RepoKit.queryAll
import uk.matvey.slon.value.Pg
import uk.matvey.slon.value.PgTimestamp.Companion.toPgTimestamp
import uk.matvey.slon.value.PgUuid.Companion.toPgUuid
import java.time.Instant
import java.util.UUID

object TagSql {

    private val log = KotlinLogging.logger {}

    const val TAGS = "$FALAFEL.tags"

    const val NAME = "name"
    const val TITLE_ID = "title_id"
    const val BALANCE_ID = "balance_id"
    const val CREATED_AT = "created_at"

    fun Repo.findAllTagsByTitleId(titleId: UUID): List<Pair<String, Int>> {
        return queryAll(
            """
                select $NAME, count(*) count
                from $TAGS
                where $TITLE_ID = ?
                group by $NAME
                order by count desc
            """.trimIndent(),
            listOf(titleId.toPgUuid())
        ) {
            it.string(NAME) to it.int("count")
        }
    }

    fun Access.addTagToTitle(accountId: UUID, tag: String, titleId: UUID) = try {
        updateSingle(update(BalanceSql.BALANCES) {
            set(BalanceSql.CURRENT, Pg.plain("${BalanceSql.CURRENT} - 1"))
            where("${BalanceSql.ACCOUNT_ID} = ? and ${BalanceSql.CURRENT} > 0", accountId.toPgUuid())
        })
        insertOneInto(TAGS) {
            set(NAME, tag)
            set(TITLE_ID, titleId)
            set(BALANCE_ID, accountId)
        }
    } catch (e: UpdateCountMismatchException) {
        log.warn(e) { "Failed to add tag $tag for title $titleId" }
    }

    fun Access.findAllBalanceIdsOfTagsAddedAfter(date: Instant): List<UUID> {
        return queryAll(
            """
                |select distinct $BALANCE_ID
                |   from $TAGS
                |   where $CREATED_AT > ?
            """.trimMargin(),
            listOf(date.toPgTimestamp())
        ) { r ->
            r.uuid(BALANCE_ID)
        }
    }

    fun readTag(reader: RecordReader): Tag {
        return Tag(
            name = reader.string(NAME),
            titleId = reader.uuid(TITLE_ID),
            balanceId = reader.uuid(BALANCE_ID),
            createdAt = reader.instant(CREATED_AT),
        )
    }
}
