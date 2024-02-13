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
) {
    
    companion object {
        
        fun from(drink: Drink, ingredients: List<Ingredient>): DrinkDetails {
            return DrinkDetails(
                drink.id,
                drink.accountId,
                drink.name,
                ingredients.associateWithTo(LinkedHashMap()) {
                    drink.ingredients.getValue(it.id)
                },
                drink.recipe,
                drink.visibility,
                drink.createdAt,
                drink.updatedAt,
            )
        }
    }
}
