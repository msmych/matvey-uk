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
import uk.matvey.slon.repo.Repo
import uk.matvey.slon.repo.RepoKit.delete
import uk.matvey.slon.repo.RepoKit.insertOneInto
import uk.matvey.slon.repo.RepoKit.queryAll
import uk.matvey.slon.repo.RepoKit.queryOne
import uk.matvey.slon.repo.RepoKit.update
import uk.matvey.slon.value.PgText.Companion.toPgText
import uk.matvey.slon.value.PgUuid.Companion.toPgUuid
import java.util.UUID

class DrinkRepo(
    private val repo: Repo,
) {

    fun add(drink: Drink) {
        repo.insertOneInto(DRINKS) {
            set(ID, drink.id)
            set(ACCOUNT_ID, drink.accountId)
            set(NAME, drink.name)
            set(INGREDIENTS, JSON.encodeToString(drink.ingredientsJson()))
            set(RECIPE, drink.recipe)
            set(VISIBILITY, drink.visibility.name)
            set(CREATED_AT, drink.createdAt)
            set(UPDATED_AT, drink.updatedAt)
        }
    }

    fun update(drink: Drink) {
        repo.update(DRINKS) {
            set(NAME, drink.name)
            set(INGREDIENTS, JSON.encodeToString(drink.ingredientsJson()))
            set(RECIPE, drink.recipe)
            set(VISIBILITY, drink.visibility.name)
            where(
                "$ID = ?",
                drink.id.toPgUuid()
            )
        }
    }

    fun delete(id: UUID) {
        repo.delete(DRINKS, "$ID = ?", id.toPgUuid())
    }

    fun get(id: UUID): Drink {
        return repo.queryOne(
            "select * from $DRINKS where $ID = ?",
            listOf(id.toPgUuid()),
            ::drink,
        )
    }

    fun search(accountId: UUID, query: String): List<Drink> {
        return repo.queryAll(
            """
                select * from $DRINKS 
                where $VISIBILITY = 'PUBLIC' 
                or $ACCOUNT_ID = ? and $NAME ilike ? 
                limit 64
                """.trimIndent(),
            listOf(accountId.toPgUuid(), "%$query%".toPgText()),
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
