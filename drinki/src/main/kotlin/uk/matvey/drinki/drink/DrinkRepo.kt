package uk.matvey.drinki.drink

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import uk.matvey.dukt.json.JsonSetup.JSON
import uk.matvey.drinki.drink.DrinkSql.ACCOUNT_ID
import uk.matvey.drinki.drink.DrinkSql.CREATED_AT
import uk.matvey.drinki.drink.DrinkSql.DRINKS
import uk.matvey.drinki.drink.DrinkSql.ID
import uk.matvey.drinki.drink.DrinkSql.INGREDIENTS
import uk.matvey.drinki.drink.DrinkSql.NAME
import uk.matvey.drinki.drink.DrinkSql.RECIPE
import uk.matvey.drinki.drink.DrinkSql.UPDATED_AT
import uk.matvey.drinki.drink.DrinkSql.VISIBILITY
import uk.matvey.drinki.ingredient.Ingredient
import uk.matvey.drinki.types.Amount
import uk.matvey.drinki.types.Visibility
import uk.matvey.postal.QueryParam.JsonbParam
import uk.matvey.postal.QueryParam.TextParam
import uk.matvey.postal.QueryParam.TimestampParam
import uk.matvey.postal.QueryParam.UuidParam
import uk.matvey.postal.QueryParams
import uk.matvey.postal.Repo
import uk.matvey.postal.ResultExtractor
import java.util.UUID

class DrinkRepo(
    private val repo: Repo,
) {
    
    fun add(drink: Drink) {
        repo.insert(
            DRINKS,
            QueryParams()
                .add(ID, UuidParam(drink.id))
                .add(ACCOUNT_ID, UuidParam(drink.accountId))
                .add(NAME, TextParam(drink.name))
                .add(INGREDIENTS, JsonbParam(JSON.encodeToString(drink.ingredientsJson())))
                .add(RECIPE, TextParam(drink.recipe))
                .add(VISIBILITY, TextParam(drink.visibility.name))
                .add(CREATED_AT, TimestampParam(drink.createdAt))
                .add(UPDATED_AT, TimestampParam(drink.updatedAt))
        )
    }
    
    fun update(drink: Drink) {
        repo.update(
            DRINKS,
            QueryParams()
                .add(NAME, TextParam(drink.name))
                .add(INGREDIENTS, JsonbParam(JSON.encodeToString(drink.ingredientsJson())))
                .add(RECIPE, TextParam(drink.recipe))
                .add(VISIBILITY, TextParam(drink.visibility.name)),
            "$ID = ?",
            QueryParams()
                .add(ID, UuidParam(drink.id)),
        )
    }
    
    fun delete(id: UUID) {
        repo.delete(DRINKS, "$ID = ?", QueryParams().add(ID, UuidParam(id)))
    }
    
    fun get(id: UUID): Drink {
        return repo.select(
            "select * from $DRINKS where $ID = ?",
            QueryParams().add(DRINKS, UuidParam(id)),
            ::drink
        )
            .single()
    }
    
    fun search(accountId: UUID, query: String): List<Drink> {
        return repo.select(
            "select * from $DRINKS where $VISIBILITY = 'PUBLIC' or $ACCOUNT_ID = ? and $NAME ilike ? limit 64",
            QueryParams()
                .add(ACCOUNT_ID, UuidParam(accountId))
                .add(NAME, TextParam("%$query%")),
            ::drink
        )
    }
    
    private fun drink(ex: ResultExtractor) = Drink(
        ex.uuid(ID),
        ex.uuid(ACCOUNT_ID),
        ex.string(NAME),
        LinkedHashMap(
            JSON.parseToJsonElement(ex.jsonb(INGREDIENTS))
                .jsonArray
                .map { it.jsonObject }
                .associate {
                    val ingredientsId = UUID.fromString(it.getValue("ingredientId").jsonPrimitive.content)
                    val amount = Amount.parse(it.getValue("amount").jsonPrimitive.content)
                    ingredientsId to amount
                }
        ),
        ex.stringOrNull(RECIPE),
        Visibility.valueOf(ex.string(VISIBILITY)),
        ex.instant(CREATED_AT),
        ex.instant(UPDATED_AT),
    )
    
    data class DrinkIngredientAmount(
        val drink: Drink,
        val ingredient: Ingredient,
        val amount: Amount
    )
}
