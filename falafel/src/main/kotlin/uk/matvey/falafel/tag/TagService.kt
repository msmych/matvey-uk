package uk.matvey.falafel.tag

import uk.matvey.falafel.tag.TagSql.findAllTagsByTitleId
import uk.matvey.slon.repo.Repo
import java.util.UUID

class TagService(
    private val repo: Repo,
) {
    private val allTags = listOf(
        TagFtl.POPCORN,
        TagFtl.SPARKLES,
        TagFtl.FACE_WITH_STUCK_OUT_TONGUE_AND_SQUINTING_EYES,
        TagFtl.EXPLODING_HEAD,
        TagFtl.GHOST,
        TagFtl.TOILET,
    ).mapIndexed { i, tag -> tag to i }
        .toMap()

    fun getTagsByTitleId(titleId: UUID): List<Pair<String, Int>> {
        val tags = repo.findAllTagsByTitleId(titleId).toMutableList()
        allTags.keys.map {
            if (tags.none { (k, _) -> k == it }) {
                tags += it to 0
            }
        }
        return tags.sortedBy { (tag, _) -> allTags[tag] }
    }
}
