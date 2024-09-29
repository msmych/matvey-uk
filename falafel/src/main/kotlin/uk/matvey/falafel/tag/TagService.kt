package uk.matvey.falafel.tag

import uk.matvey.falafel.tag.TagSql.findAllTagsByTitleId
import uk.matvey.slon.repo.Repo
import java.util.UUID

class TagService(
    private val repo: Repo,
) {

    fun getTagsByTitleId(titleId: UUID): Map<String, Int> {
        val tags = repo.findAllTagsByTitleId(titleId).toMutableMap()
        tags.putIfAbsent(Tags.POPCORN, 0)
        return tags
    }
}
