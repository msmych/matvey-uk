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
import uk.matvey.drinki.types.Amount
import uk.matvey.drinki.types.Visibility
import uk.matvey.kit.json.JsonKit.JSON
import uk.matvey.slon.RecordReader
import uk.matvey.slon.Repo
import uk.matvey.slon.param.JsonbParam.Companion.jsonb
import uk.matvey.slon.param.TextParam.Companion.text
import uk.matvey.slon.param.TimestampParam.Companion.timestamp
import uk.matvey.slon.param.UuidParam.Companion.uuid
import uk.matvey.slon.query.update.DeleteQuery.Builder.Companion.deleteFrom
import uk.matvey.slon.query.update.UpdateQuery.Builder.Companion.update
import java.util.UUID

class DrinkRepo(
    private val repo: Repo,
) {

    fun add(drink: Drink) {
        repo.insertOne(
            DRINKS,
            ID to uuid(drink.id),
            ACCOUNT_ID to uuid(drink.accountId),
            NAME to text(drink.name),
            INGREDIENTS to jsonb(JSON.encodeToString(drink.ingredientsJson())),
            RECIPE to text(drink.recipe),
            VISIBILITY to text(drink.visibility.name),
            CREATED_AT to timestamp(drink.createdAt),
            UPDATED_AT to timestamp(drink.updatedAt),
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
        repo.access { a -> a.execute(deleteFrom(DRINKS).where("$ID = ?", uuid(id))) }
    }

    fun get(id: UUID): Drink {
        return repo.queryOne(
            "select * from $DRINKS where $ID = ?",
            listOf(uuid(id)),
            ::drink
        )
    }

    fun search(accountId: UUID, query: String): List<Drink> {
        return repo.query(
            """
                select * from $DRINKS 
                where $VISIBILITY = 'PUBLIC' 
                or $ACCOUNT_ID = ? and $NAME ilike ? 
                limit 64
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
}
