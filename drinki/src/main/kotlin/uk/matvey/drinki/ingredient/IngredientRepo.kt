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
import uk.matvey.slon.QueryParam.Companion.text
import uk.matvey.slon.QueryParam.Companion.timestamp
import uk.matvey.slon.QueryParam.Companion.uuid
import uk.matvey.slon.Repo
import uk.matvey.slon.command.Insert.Builder.Companion.insert
import uk.matvey.slon.command.Update.Builder.Companion.update
import uk.matvey.slon.query.RecordReader
import java.util.UUID

class IngredientRepo(
    private val repo: Repo,
) {

    fun add(ingredient: Ingredient) {
        repo.execute(
            insert(INGREDIENTS)
                .values(
                    ID to uuid(ingredient.id),
                    ACCOUNT_ID to uuid(ingredient.accountId),
                    TYPE to text(ingredient.type?.name),
                    NAME to text(ingredient.name),
                    VISIBILITY to text(ingredient.visibility.name),
                    CREATED_AT to timestamp(ingredient.createdAt),
                    UPDATED_AT to timestamp(ingredient.updatedAt),
                )
        )
    }

    fun update(ingredient: Ingredient) {
        repo.execute(
            update(INGREDIENTS)
                .set(NAME to text(ingredient.name))
                .where("$ID = ?", uuid(ingredient.id))
        )
    }

    fun get(ingredientId: UUID): Ingredient {
        return repo.query(
            "SELECT * FROM $INGREDIENTS WHERE $ID = ?",
            listOf(uuid(ingredientId)),
            ::ingredient
        ).single()
    }

    fun findAllByAccountId(accountId: UUID?): List<Ingredient> {
        val accountIdCondition = accountId?.let { "$ACCOUNT_ID = ?" } ?: "$ACCOUNT_ID is null"
        return repo.query(
            """
                SELECT * FROM $INGREDIENTS 
                WHERE $accountIdCondition 
                ORDER BY $TYPE NULLS LAST, $NAME
                """.trimIndent(),
            listOfNotNull(accountId?.let { uuid(it) }),
            ::ingredient
        )
    }

    fun publicIngredients(): List<Ingredient> {
        return findAllByAccountId(null)
    }

    fun findAllByDrink(drinkId: UUID): List<Ingredient> {
        return repo.query(
            """
            SELECT * FROM $INGREDIENTS 
            WHERE $ID IN (
                SELECT (jsonb_array_elements(${DrinkSql.INGREDIENTS}) ->> 'ingredientId')::UUID 
                FROM ${DrinkSql.DRINKS} WHERE ${DrinkSql.ID} = ?
            )
            """.trimIndent(),
            listOf(uuid(drinkId)),
            ::ingredient
        )
    }

    private fun ingredient(reader: RecordReader): Ingredient {
        return Ingredient(
            reader.uuid(ID),
            reader.nullableUuid(ACCOUNT_ID),
            reader.nullableString(TYPE)?.let(Ingredient.Type::valueOf),
            reader.string(NAME),
            Visibility.valueOf(reader.string(VISIBILITY)),
            reader.instant(CREATED_AT),
            reader.instant(UPDATED_AT),
        )
    }
}
