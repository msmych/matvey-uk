package uk.matvey.drinki.drink

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
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
import uk.matvey.dukt.json.JsonSetup.JSON
import uk.matvey.slon.QueryParam.Companion.jsonb
import uk.matvey.slon.QueryParam.Companion.text
import uk.matvey.slon.QueryParam.Companion.timestamp
import uk.matvey.slon.QueryParam.Companion.uuid
import uk.matvey.slon.Repo
import uk.matvey.slon.command.Delete.Builder.Companion.delete
import uk.matvey.slon.command.Insert.Builder.Companion.insert
import uk.matvey.slon.command.Update.Builder.Companion.update
import uk.matvey.slon.query.RecordReader
import java.util.UUID

class DrinkRepo(
    private val repo: Repo,
) {

    fun add(drink: Drink) {
        repo.execute(
            insert(DRINKS)
                .values(
                    ID to uuid(drink.id),
                    ACCOUNT_ID to uuid(drink.accountId),
                    NAME to text(drink.name),
                    INGREDIENTS to jsonb(JSON.encodeToString(drink.ingredientsJson())),
                    RECIPE to text(drink.recipe),
                    VISIBILITY to text(drink.visibility.name),
                    CREATED_AT to timestamp(drink.createdAt),
                    UPDATED_AT to timestamp(drink.updatedAt),
                )
        )
    }

    fun update(drink: Drink) {
        repo.execute(
            update(DRINKS)
                .set(
                    NAME to text(drink.name),
                    INGREDIENTS to jsonb(JSON.encodeToString(drink.ingredientsJson())),
                    RECIPE to text(drink.recipe),
                    VISIBILITY to text(drink.visibility.name)
                )
                .where("$ID = ?", uuid(drink.id))
        )
    }

    fun delete(id: UUID) {
        repo.execute(delete(DRINKS).where("$ID = ?", uuid(id)))
    }

    fun get(id: UUID): Drink {
        return repo.query(
            "SELECT * FROM $DRINKS WHERE $ID = ?",
            listOf(uuid(id)),
            ::drink
        )
            .single()
    }

    fun search(accountId: UUID, query: String): List<Drink> {
        return repo.query(
            """
                SELECT * FROM $DRINKS 
                WHERE $VISIBILITY = 'PUBLIC' 
                OR $ACCOUNT_ID = ? AND $NAME ILIKE ? 
                LIMIT 64
                """.trimIndent(),
            listOf(
                uuid(accountId),
                text("%$query%")
            ),
            ::drink
        )
    }

    private fun drink(reader: RecordReader) = Drink(
        reader.uuid(ID),
        reader.uuid(ACCOUNT_ID),
        reader.string(NAME),
        LinkedHashMap(
            JSON.parseToJsonElement(reader.string(INGREDIENTS))
                .jsonArray
                .map { it.jsonObject }
                .associate {
                    val ingredientsId = UUID.fromString(it.getValue("ingredientId").jsonPrimitive.content)
                    val amount = Amount.parse(it.getValue("amount").jsonPrimitive.content)
                    ingredientsId to amount
                }
        ),
        reader.nullableString(RECIPE),
        Visibility.valueOf(reader.string(VISIBILITY)),
        reader.instant(CREATED_AT),
        reader.instant(UPDATED_AT),
    )

    data class DrinkIngredientAmount(
        val drink: Drink,
        val ingredient: Ingredient,
        val amount: Amount
    )
}
