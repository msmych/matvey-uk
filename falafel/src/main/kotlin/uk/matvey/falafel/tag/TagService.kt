package uk.matvey.falafel.tag

import uk.matvey.falafel.tag.TagSql.findAllTagsByTitleId
import uk.matvey.slon.repo.Repo
import java.util.UUID

class TagService(
    private val repo: Repo,
) {

    fun getTagsByTitleId(titleId: UUID): Map<String, Int> {
        val tags = repo.findAllTagsByTitleId(titleId).toMutableList()
        listOf(
            Tags.POPCORN,
            Tags.SPARKLES,
            Tags.FACE_WITH_STUCK_OUT_TONGUE_AND_SQUINTING_EYES,
            Tags.EXPLODING_HEAD,
            Tags.GHOST,
            Tags.TOILET,
        ).map {
            if (tags.none { (k, _) -> k == it }) {
                tags += it to 0
            }
        }
        return tags.toMap()
    }
}
