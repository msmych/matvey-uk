package uk.matvey.falafel.tag

import uk.matvey.falafel.FalafelSql.FALAFEL
import uk.matvey.slon.repo.Repo
import uk.matvey.slon.repo.RepoKit.queryAll
import uk.matvey.slon.value.PgUuid.Companion.toPgUuid
import java.util.UUID

object TagSql {

    const val TAGS = "$FALAFEL.tags"

    const val NAME = "name"
    const val TITLE_ID = "title_id"

    fun Repo.findAllTagsByTitleId(titleId: UUID): Map<String, Int> {
        return queryAll(
            """
                select name, count(*) as count
                from $TAGS
                where $TITLE_ID = ?
                group by $NAME
            """.trimIndent(),
            listOf(titleId.toPgUuid())
        ) {
            it.string(1) to it.int(2)
        }.toMap()
    }
}
