package uk.matvey.telek

object TgSupport {

    fun escapeSpecial(s: String): String {
        return s
            .replace(".", "\\.")
            .replace("!", "\\!")
            .replace("-", "\\-")
    }
}