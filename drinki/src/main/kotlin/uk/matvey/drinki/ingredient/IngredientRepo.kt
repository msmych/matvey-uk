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
import uk.matvey.slon.access.AccessKit.queryAll
import uk.matvey.slon.access.AccessKit.queryOne
import uk.matvey.slon.query.InsertOneQueryBuilder.Companion.insertOneInto
import uk.matvey.slon.query.UpdateQueryBuilder.Companion.update
import uk.matvey.slon.repo.Repo
import uk.matvey.slon.value.PgUuid.Companion.toPgUuid
import java.util.UUID

class IngredientRepo(
    private val repo: Repo,
) {

    suspend fun add(ingredient: Ingredient) {
        repo.access { a ->
            a.execute(
                insertOneInto(INGREDIENTS) {
                    set(ID, ingredient.id)
                    set(ACCOUNT_ID, ingredient.accountId)
                    set(TYPE, ingredient.type?.name)
                    set(NAME, ingredient.name)
                    set(VISIBILITY, ingredient.visibility.name)
                    set(CREATED_AT, ingredient.createdAt)
                    set(UPDATED_AT, ingredient.updatedAt)
                }
            )
        }
    }

    suspend fun update(ingredient: Ingredient) {
        repo.access { a ->
            a.execute(
                update(INGREDIENTS) {
                    set(NAME, ingredient.name)
                    where("$ID = ?", ingredient.id.toPgUuid())
                }
            )
        }
    }

    suspend fun get(ingredientId: UUID): Ingredient {
        return repo.access { a ->
            a.queryOne(
                "select * from $INGREDIENTS where $ID = ?",
                listOf(ingredientId.toPgUuid()),
                ::ingredient
            )
        }
    }

    suspend fun findAllByAccountId(accountId: UUID?): List<Ingredient> {
        val accountIdCondition = accountId?.let { "$ACCOUNT_ID = ?" } ?: "$ACCOUNT_ID is null"
        return repo.access { a ->
            a.queryAll(
                """
                select * from $INGREDIENTS 
                where $accountIdCondition 
                order by $TYPE nulls last, $NAME
                """.trimIndent(),
                listOfNotNull(accountId?.toPgUuid()),
                ::ingredient
            )
        }
    }

    suspend fun publicIngredients(): List<Ingredient> {
        return findAllByAccountId(null)
    }

    suspend fun findAllByDrink(drinkId: UUID): List<Ingredient> {
        return repo.access { a ->
            a.queryAll(
                """
            select * from $INGREDIENTS 
            where $ID in (
                select (jsonb_array_elements(${DrinkSql.INGREDIENTS}) ->> 'ingredientId')::uuid 
                from ${DrinkSql.DRINKS} where ${DrinkSql.ID} = ?
            )
            """.trimIndent(),
                listOf(drinkId.toPgUuid()),
                ::ingredient
            )
        }
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
