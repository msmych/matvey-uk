package uk.matvey.telek

object TgSupport {
    
    fun String.tgEscape(): String {
        return this
            .replace(".", "\\.")
            .replace("!", "\\!")
            .replace("-", "\\-")
            .replace("(", "\\(")
            .replace(")", "\\)")
            .replace("[", "\\[")
            .replace("]", "\\]")
    }
}
