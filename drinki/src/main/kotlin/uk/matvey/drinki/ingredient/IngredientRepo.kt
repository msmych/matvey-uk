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
import uk.matvey.slon.RecordReader
import uk.matvey.slon.param.TextParam.Companion.text
import uk.matvey.slon.param.TimestampParam.Companion.timestamp
import uk.matvey.slon.param.UuidParam.Companion.uuid
import uk.matvey.slon.repo.Repo
import uk.matvey.slon.repo.RepoKit.insertInto
import uk.matvey.slon.repo.RepoKit.query
import uk.matvey.slon.repo.RepoKit.queryOne
import uk.matvey.slon.repo.RepoKit.update
import java.util.UUID

class IngredientRepo(
    private val repo: Repo,
) {

    suspend fun add(ingredient: Ingredient) {
        repo.insertInto(INGREDIENTS) {
            values(
                ID to uuid(ingredient.id),
                ACCOUNT_ID to uuid(ingredient.accountId),
                TYPE to text(ingredient.type?.name),
                NAME to text(ingredient.name),
                VISIBILITY to text(ingredient.visibility.name),
                CREATED_AT to timestamp(ingredient.createdAt),
                UPDATED_AT to timestamp(ingredient.updatedAt),
            )
        }
    }

    suspend fun update(ingredient: Ingredient) {
        repo.update(INGREDIENTS) {
            set(NAME, text(ingredient.name))
            where("$ID = ?", uuid(ingredient.id))
        }
    }

    suspend fun get(ingredientId: UUID): Ingredient {
        return repo.queryOne(
            "select * from $INGREDIENTS where $ID = ?",
            listOf(uuid(ingredientId)),
            ::ingredient
        )
    }

    suspend fun findAllByAccountId(accountId: UUID?): List<Ingredient> {
        val accountIdCondition = accountId?.let { "$ACCOUNT_ID = ?" } ?: "$ACCOUNT_ID is null"
        return repo.query(
            """
                select * from $INGREDIENTS 
                where $accountIdCondition 
                order by $TYPE nulls last, $NAME
                """.trimIndent(),
            listOfNotNull(accountId?.let { uuid(it) }),
            ::ingredient
        )
    }

    suspend fun publicIngredients(): List<Ingredient> {
        return findAllByAccountId(null)
    }

    suspend fun findAllByDrink(drinkId: UUID): List<Ingredient> {
        return repo.query(
            """
            select * from $INGREDIENTS 
            where $ID in (
                select (jsonb_array_elements(${DrinkSql.INGREDIENTS}) ->> 'ingredientId')::uuid 
                from ${DrinkSql.DRINKS} where ${DrinkSql.ID} = ?
            )
            """.trimIndent(),
            listOf(uuid(drinkId)),
            ::ingredient
        )
    }

    private fun ingredient(reader: RecordReader): Ingredient {
        return Ingredient(
            reader.uuid(ID),
            reader.uuidOrNull(ACCOUNT_ID),
            reader.stringOrNull(TYPE)?.let(Ingredient.Type::valueOf),
            reader.string(NAME),
            Visibility.valueOf(reader.string(VISIBILITY)),
            reader.instant(CREATED_AT),
            reader.instant(UPDATED_AT),
        )
    }
}
