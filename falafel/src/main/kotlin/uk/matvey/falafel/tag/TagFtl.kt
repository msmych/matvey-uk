package uk.matvey.falafel.tag

import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.util.pipeline.PipelineContext
import uk.matvey.falafel.balance.AccountBalance
import uk.matvey.utka.ktor.ftl.FreeMarkerKit.respondFtl

object TagFtl {

    private const val BASE_PATH = "/falafel/tags"

    const val POPCORN = "popcorn"
    const val SPARKLES = "sparkles"
    const val FACE_WITH_STUCK_OUT_TONGUE_AND_SQUINTING_EYES = "face_with_stuck_out_tongue_and_squinting_eyes"
    const val EXPLODING_HEAD = "exploding_head"
    const val GHOST = "ghost"
    const val TOILET = "toilet"

    val TAGS_EMOJIS = mapOf(
        POPCORN to "🍿",
        SPARKLES to "✨",
        FACE_WITH_STUCK_OUT_TONGUE_AND_SQUINTING_EYES to "😝",
        EXPLODING_HEAD to "🤯",
        GHOST to "👻",
        TOILET to "🚽",
    )

    data class TagCount(
        val name: String,
        val count: Int,
        val emoji: String,
    ) {

        companion object {
            fun from(name: String, count: Int): TagCount {
                return TagCount(name, count, TAGS_EMOJIS.getValue(name))
            }
        }
    }

    suspend fun PipelineContext<Unit, ApplicationCall>.respondTagsView(
        tags: List<Pair<String, Int>>,
        account: AccountBalance
    ) {
        call.respondFtl(
            "$BASE_PATH/tags-view",
            "tags" to tags.map { (name, count) -> TagCount.from(name, count) },
            "account" to account
        )
    }

}
