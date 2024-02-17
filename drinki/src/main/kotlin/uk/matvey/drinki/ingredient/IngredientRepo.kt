package uk.matvey.drinki.ingredient

import uk.matvey.drinki.drink.DrinkSql
import uk.matvey.drinki.drink.DrinkSql.VISIBILITY
import uk.matvey.drinki.ingredient.IngredientSql.ACCOUNT_ID
import uk.matvey.drinki.ingredient.IngredientSql.CREATED_AT
import uk.matvey.drinki.ingredient.IngredientSql.ID
import uk.matvey.drinki.ingredient.IngredientSql.INGREDIENTS
import uk.matvey.drinki.ingredient.IngredientSql.NAME
import uk.matvey.drinki.ingredient.IngredientSql.TYPE
import uk.matvey.drinki.ingredient.IngredientSql.UPDATED_AT
import uk.matvey.drinki.types.Visibility
import uk.matvey.postal.QueryParam.TextParam
import uk.matvey.postal.QueryParam.TimestampParam
import uk.matvey.postal.QueryParam.UuidParam
import uk.matvey.postal.QueryParams
import uk.matvey.postal.Repo
import uk.matvey.postal.ResultExtractor
import java.util.UUID

class IngredientRepo(
    private val repo: Repo,
) {
    
    fun add(ingredient: Ingredient) {
        repo.insert(
            INGREDIENTS,
            QueryParams()
                .add(ID, UuidParam(ingredient.id))
                .add(ACCOUNT_ID, UuidParam(ingredient.accountId))
                .add(TYPE, TextParam(ingredient.type?.name))
                .add(NAME, TextParam(ingredient.name))
                .add(VISIBILITY, TextParam(ingredient.visibility.name))
                .add(CREATED_AT, TimestampParam(ingredient.createdAt))
                .add(UPDATED_AT, TimestampParam(ingredient.updatedAt))
        )
    }
    
    fun update(ingredient: Ingredient) {
        repo.update(
            INGREDIENTS,
            QueryParams()
                .add(NAME, TextParam(ingredient.name)),
            "$ID = ?",
            QueryParams()
                .add(ID, UuidParam(ingredient.id))
        )
    }
    
    fun get(ingredientId: UUID): Ingredient {
        return repo.select(
            "select * from $INGREDIENTS where $ID = ?",
            QueryParams().add(ID, UuidParam(ingredientId)),
            ::ingredient
        ).single()
    }
    
    fun findAllByAccountId(accountId: UUID?): List<Ingredient> {
        val accountIdCondition = accountId?.let { "$ACCOUNT_ID = ?" } ?: "$ACCOUNT_ID is null"
        return repo.select(
            "select * from $INGREDIENTS where $accountIdCondition order by $TYPE nulls last, $NAME",
            QueryParams().apply {
                accountId?.let { add(ACCOUNT_ID, UuidParam(it)) }
            },
            ::ingredient
        )
    }
    
    fun publicIngredients(): List<Ingredient> {
        return findAllByAccountId(null)
    }
    
    fun findAllByDrink(drinkId: UUID): List<Ingredient> {
        return repo.select(
            """
            select * from $INGREDIENTS 
            where $ID in (
                select (jsonb_array_elements(${DrinkSql.INGREDIENTS}) ->> 'ingredientId')::uuid 
                from ${DrinkSql.DRINKS} where ${DrinkSql.ID} = ?
            )
            """.trimIndent(),
            QueryParams()
                .add(DrinkSql.ID, UuidParam(drinkId)),
            ::ingredient
        )
    }
    
    private fun ingredient(ex: ResultExtractor): Ingredient {
        return Ingredient(
            ex.uuid(ID),
            ex.uuidOrNull(ACCOUNT_ID),
            ex.stringOrNull(TYPE)?.let(Ingredient.Type::valueOf),
            ex.string(NAME),
            Visibility.valueOf(ex.string(VISIBILITY)),
            ex.instant(CREATED_AT),
            ex.instant(UPDATED_AT),
        )
    }
}
