package uk.matvey.drinki.ingredient

import uk.matvey.drinki.types.Visibility
import java.time.Instant
import java.util.*
import java.util.UUID.randomUUID

data class Ingredient(
    val id: UUID,
    val accountId: UUID?,
    val type: Type?,
    val name: String,
    val visibility: Visibility,
    val createdAt: Instant,
    val updatedAt: Instant,
) {

    enum class Type {
        SPIRIT,
        BITTER,
        WINE,
        JUICE,
        SYRUP,
        TONIC,
        OTHER,
        ;
    }

    companion object {

        fun public(type: Type, name: String): Ingredient {
            val now = Instant.now()
            return Ingredient(
                randomUUID(),
                null,
                type,
                name,
                Visibility.PUBLIC,
                now,
                now
            )
        }

        fun private(accountId: UUID, name: String): Ingredient {
            val now = Instant.now()
            return Ingredient(
                randomUUID(),
                accountId,
                null,
                name,
                Visibility.PRIVATE,
                now,
                now
            )
        }
    }
}
