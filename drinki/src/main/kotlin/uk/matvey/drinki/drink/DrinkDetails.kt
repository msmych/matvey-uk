package uk.matvey.drinki.drink

import uk.matvey.drinki.ingredient.Ingredient
import uk.matvey.drinki.types.Amount

data class DrinkDetails(
    val drink: Drink,
    val ingredients: LinkedHashMap<Ingredient, Amount>,
) {
    
    fun id() = drink.id
    
    fun name() = drink.name
    
    fun recipe() = drink.recipe
    
    fun visibility() = drink.visibility
    
    companion object {
        
        fun from(drink: Drink, ingredients: List<Ingredient>): DrinkDetails {
            return DrinkDetails(
                drink,
                ingredients.associateWithTo(LinkedHashMap()) {
                    drink.ingredients.getValue(it.id)
                },
            )
        }
    }
}
