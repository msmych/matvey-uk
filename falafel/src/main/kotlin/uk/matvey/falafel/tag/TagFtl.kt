package uk.matvey.falafel.tag

object TagFtl {

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
}
