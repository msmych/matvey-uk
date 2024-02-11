package uk.matvey.drinki.types

enum class Visibility {
    PRIVATE,
    PUBLIC,
    ;

    fun toggle() = when (this) {
        PRIVATE -> PUBLIC
        PUBLIC -> PRIVATE
    }
}