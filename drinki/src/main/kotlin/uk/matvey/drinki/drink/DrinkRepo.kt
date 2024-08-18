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
import uk.matvey.kit.string.StringKit.toUuid
import uk.matvey.slon.RecordReader
import uk.matvey.slon.param.JsonbParam.Companion.jsonb
import uk.matvey.slon.param.TextParam.Companion.text
import uk.matvey.slon.param.TimestampParam.Companion.timestamp
import uk.matvey.slon.param.UuidParam.Companion.uuid
import uk.matvey.slon.query.update.DeleteQueryBuilder.Companion.deleteFrom
import uk.matvey.slon.repo.Repo
import uk.matvey.slon.repo.RepoKit.execute
import uk.matvey.slon.repo.RepoKit.insertInto
import uk.matvey.slon.repo.RepoKit.query
import uk.matvey.slon.repo.RepoKit.queryOne
import uk.matvey.slon.repo.RepoKit.update
import java.util.UUID

class DrinkRepo(
    private val repo: Repo,
) {

    suspend fun add(drink: Drink) {
        repo.insertInto(DRINKS) {
            values(
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
    }

    suspend fun update(drink: Drink) {
        repo.update(DRINKS) {
            set(NAME, text(drink.name))
            set(INGREDIENTS, jsonb(JSON.encodeToString(drink.ingredientsJson())))
            set(RECIPE, text(drink.recipe))
            set(VISIBILITY, text(drink.visibility.name))
            where("$ID = ?", uuid(drink.id))
        }
    }

    suspend fun delete(id: UUID) {
        repo.execute(deleteFrom(DRINKS).where("$ID = ?", uuid(id)))
    }

    suspend fun get(id: UUID): Drink {
        return repo.queryOne(
            "select * from $DRINKS where $ID = ?",
            listOf(uuid(id)),
            ::drink
        )
    }

    suspend fun search(accountId: UUID, query: String): List<Drink> {
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
                    val ingredientsId = it.getValue("ingredientId").jsonPrimitive.content.toUuid()
                    val amount = Amount.parse(it.getValue("amount").jsonPrimitive.content)
                    ingredientsId to amount
                }
        ),
        reader.stringOrNull(RECIPE),
        Visibility.valueOf(reader.string(VISIBILITY)),
        reader.instant(CREATED_AT),
        reader.instant(UPDATED_AT),
    )
}
