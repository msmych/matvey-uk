package uk.matvey.drinki.drink

import uk.matvey.drinki.ingredient.Ingredient
import uk.matvey.drinki.types.Amount
import uk.matvey.drinki.types.Visibility
import java.time.Instant
import java.util.UUID

data class DrinkDetails(
    val id: UUID,
    val accountId: UUID?,
    val name: String,
    val ingredients: LinkedHashMap<Ingredient, Amount>,
    val recipe: String?,
    val visibility: Visibility,
    val createdAt: Instant,
    val updatedAt: Instant,
)
