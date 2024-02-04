package uk.matvey.drinki.drink

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.addJsonObject
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.put
import uk.matvey.drinki.types.Amount
import uk.matvey.drinki.types.Visibility
import java.time.Instant
import java.util.*
import java.util.UUID.randomUUID

data class Drink(
    val id: UUID,
    val accountId: UUID?,
    val name: String,
    val ingredients: List<Pair<UUID, Amount>>,
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
        val ingredients = LinkedHashMap(this.ingredients.toMap())
        ingredients[ingredientId] = amount
        return this.copy(
            ingredients = ingredients.map { (k, v) -> k to v }
        )
    }

    fun deleteIngredient(ingredientId: UUID): Drink {
        val ingredients = LinkedHashMap(this.ingredients.toMap())
        ingredients -= ingredientId
        return this.copy(
            ingredients = ingredients.map { (k, v) -> k to v }
        )
    }

    fun setRecipe(recipe: String): Drink {
        return this.copy(recipe = recipe)
    }

    fun toggleVisibility(): Drink {
        return this.copy(
            visibility = when (this.visibility) {
                Visibility.PRIVATE -> Visibility.PUBLIC
                Visibility.PUBLIC -> Visibility.PRIVATE
            }
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
                listOf(),
                null,
                Visibility.PRIVATE,
                now,
                now
            )
        }
    }
}
