package uk.matvey.drinki.drink

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import uk.matvey.drinki.Setup.JSON
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

    fun getDetails(id: UUID): DrinkDetails {
        val drinksIngredients = repo.select(
            """
            with di as (select id                                               as d_id,
                   (jsonb_array_elements(ingredients) ->> 'ingredientId')::uuid as i_id,
                   (jsonb_array_elements(ingredients) ->> 'amount')             as amount
            from drinki.drinks)

            select d.id         as d_id,
                   d.account_id as d_account_id,
                   d.name       as d_name,
                   d.recipe     as d_recipe,
                   d.visibility as d_visibility,
                   d.created_at as d_created_at,
                   d.updated_at as d_updated_at,
                   i.id         as i_id,
                   i.account_id as i_account_id,
                   i.type       as i_type,
                   i.name       as i_name,
                   i.visibility as i_visibility,
                   i.created_at as i_created_at,
                   i.updated_at as i_updated_at,
                   di.amount    as i_amount
            from di
                     join drinki.drinks d on d.id = di.d_id
                     join drinki.ingredients i on i.id = di.i_id
            where d.id = ?
        """.trimIndent(),
            QueryParams().add(ID, UuidParam(id)),
            ::drinkIngredientAmount
        )
        val (drink, _, _) = drinksIngredients.first()
        return DrinkDetails(
            drink.id,
            drink.accountId,
            drink.name,
            LinkedHashMap(drinksIngredients.associate { (_, ingredient, amount) -> ingredient to amount }),
            drink.recipe,
            drink.visibility,
            drink.createdAt,
            drink.updatedAt
        )
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

    private fun drinkIngredientAmount(ex: ResultExtractor): DrinkIngredientAmount {
        return DrinkIngredientAmount(
            Drink(
                ex.uuid("d_id"),
                ex.uuid("d_account_id"),
                ex.string("d_name"),
                linkedMapOf(),
                ex.stringOrNull("d_recipe"),
                Visibility.valueOf(ex.string("d_visibility")),
                ex.instant("d_created_at"),
                ex.instant("d_updated_at")
            ),
            Ingredient(
                ex.uuid("i_id"),
                ex.uuidOrNull("i_account_id"),
                ex.stringOrNull("i_type")?.let(Ingredient.Type::valueOf),
                ex.string("i_name"),
                Visibility.valueOf(ex.string("i_visibility")),
                ex.instant("i_created_at"),
                ex.instant("i_updated_at")
            ),
            Amount.parse(ex.string("i_amount")),
        )
    }

    data class DrinkIngredientAmount(
        val drink: Drink,
        val ingredient: Ingredient,
        val amount: Amount
    )
}
