package uk.matvey.drinki.drink

import uk.matvey.drinki.ingredient.IngredientRepo
import java.util.UUID

class DrinkService(
    private val drinkRepo: DrinkRepo,
    private val ingredientRepo: IngredientRepo,
) {
    
    fun getDrinkDetails(id: UUID): DrinkDetails {
        val drink = drinkRepo.get(id)
        val ingredients = ingredientRepo.findAllByDrink(drink.id)
        return DrinkDetails.from(drink, ingredients)
    }
}
