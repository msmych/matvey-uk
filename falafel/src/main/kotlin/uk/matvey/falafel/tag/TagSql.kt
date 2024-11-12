package uk.matvey.falafel.tag

import mu.KotlinLogging
import uk.matvey.app.MatveySql.ID
import uk.matvey.falafel.FalafelSql.FALAFEL
import uk.matvey.falafel.balance.AccountSql
import uk.matvey.slon.RecordReader
import uk.matvey.slon.access.Access
import uk.matvey.slon.access.AccessKit.insertOneInto
import uk.matvey.slon.access.AccessKit.updateSingle
import uk.matvey.slon.exception.UpdateCountMismatchException
import uk.matvey.slon.query.UpdateQueryBuilder.Companion.update
import uk.matvey.slon.repo.Repo
import uk.matvey.slon.repo.RepoKit.queryAll
import uk.matvey.slon.value.Pg
import uk.matvey.slon.value.PgUuid.Companion.toPgUuid
import java.util.UUID

object TagSql {

    private val log = KotlinLogging.logger {}

    const val TAGS = "$FALAFEL.tags"

    const val NAME = "name"
    const val TITLE_ID = "title_id"
    const val ACCOUNT_ID = "account_id"
    const val CREATED_AT = "created_at"

    fun Repo.findAllTagsByTitleId(titleId: UUID): List<Pair<String, Int>> {
        return queryAll(
            """
                select $NAME, count(*) count
                from $TAGS
                where $TITLE_ID = ?
                group by $NAME
            """.trimIndent(),
            listOf(titleId.toPgUuid())
        ) {
            it.string(NAME) to it.int("count")
        }
    }

    fun Access.addTagToTitle(accountId: UUID, tag: String, titleId: UUID) = try {
        updateSingle(update(AccountSql.ACCOUNTS) {
            set(AccountSql.BALANCE, Pg.plain("${AccountSql.BALANCE} - 1"))
            where("$ID = ? and ${AccountSql.BALANCE} > 0", accountId.toPgUuid())
        })
        insertOneInto(TAGS) {
            set(NAME, tag)
            set(TITLE_ID, titleId)
            set(ACCOUNT_ID, accountId)
        }
    } catch (e: UpdateCountMismatchException) {
        log.warn(e) { "Failed to add tag $tag for title $titleId" }
    }

    fun readTag(reader: RecordReader): Tag {
        return Tag(
            name = reader.string(NAME),
            titleId = reader.uuid(TITLE_ID),
            balanceId = reader.uuid(ACCOUNT_ID),
            createdAt = reader.instant(CREATED_AT),
        )
    }
}
