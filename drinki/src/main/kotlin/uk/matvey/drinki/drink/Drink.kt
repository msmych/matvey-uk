package uk.matvey.drinki.drink

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.addJsonObject
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.put
import uk.matvey.drinki.types.Amount
import uk.matvey.drinki.types.Visibility
import java.time.Instant
import java.util.UUID
import java.util.UUID.randomUUID

data class Drink(
    val id: UUID,
    val accountId: UUID?,
    val name: String,
    val ingredients: LinkedHashMap<UUID, Amount>,
    val recipe: String?,
    val visibility: Visibility,
    val createdAt: Instant,
    val updatedAt: Instant,
) {

    fun ingredientAmount(ingredientId: UUID): Amount? {
        return this.ingredients.toMap()[ingredientId]
    }

    fun setName(name: String): Drink {
        return this.copy(name = name)
    }

    fun setIngredient(ingredientId: UUID, amount: Amount): Drink {
        this.ingredients[ingredientId] = amount
        return this.copy(
            ingredients = this.ingredients
        )
    }

    fun deleteIngredient(ingredientId: UUID): Drink {
        this.ingredients -= ingredientId
        return this.copy(
            ingredients = this.ingredients
        )
    }

    fun setRecipe(recipe: String): Drink {
        return this.copy(recipe = recipe)
    }

    fun toggleVisibility(): Drink {
        return this.copy(
            visibility = this.visibility.toggle()
        )
    }

    fun ingredientsJson(): JsonArray {
        return buildJsonArray {
            ingredients.map { (k, v) ->
                addJsonObject {
                    put("ingredientId", k.toString())
                    put("amount", v.toString())
                }
            }
        }
    }

    companion object {
        fun new(accountId: UUID): Drink {
            val now = Instant.now()
            return Drink(
                randomUUID(),
                accountId,
                "New drink",
                linkedMapOf(),
                null,
                Visibility.PRIVATE,
                now,
                now
            )
        }
    }
}
