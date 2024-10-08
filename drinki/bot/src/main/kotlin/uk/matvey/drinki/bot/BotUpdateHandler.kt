package uk.matvey.drinki.bot

import mu.KotlinLogging
import uk.matvey.drinki.account.AccountService
import uk.matvey.drinki.bot.drink.AddDrink
import uk.matvey.drinki.bot.drink.AddDrinkIngredient
import uk.matvey.drinki.bot.drink.DeleteDrink
import uk.matvey.drinki.bot.drink.DeleteDrinkIngredient
import uk.matvey.drinki.bot.drink.EditDrink
import uk.matvey.drinki.bot.drink.EditDrinkIngredientAmount
import uk.matvey.drinki.bot.drink.EditDrinkIngredients
import uk.matvey.drinki.bot.drink.EditDrinkName
import uk.matvey.drinki.bot.drink.EditDrinkRecipe
import uk.matvey.drinki.bot.drink.SearchDrinks
import uk.matvey.drinki.bot.drink.SetDrinkIngredientAmount
import uk.matvey.drinki.bot.drink.SetDrinkName
import uk.matvey.drinki.bot.drink.SetDrinkRecipe
import uk.matvey.drinki.bot.drink.ToggleDrinkVisibility
import uk.matvey.drinki.bot.ingredient.AddIngredient
import uk.matvey.drinki.bot.ingredient.EditIngredientName
import uk.matvey.drinki.bot.ingredient.EditIngredientType
import uk.matvey.drinki.bot.ingredient.GetIngredients
import uk.matvey.drinki.bot.ingredient.SetIngredientName
import uk.matvey.drinki.bot.ingredient.SetIngredientType
import uk.matvey.drinki.bot.ingredient.ToggleIngredientVisibility
import uk.matvey.drinki.ingredient.Ingredient
import uk.matvey.drinki.types.Amount
import uk.matvey.telek.TgRequest
import java.util.UUID

class BotUpdateHandler(
    private val greet: Greet,
    private val addDrink: AddDrink,
    private val getIngredients: GetIngredients,
    private val editDrink: EditDrink,
    private val editDrinkName: EditDrinkName,
    private val editDrinkIngredientAmount: EditDrinkIngredientAmount,
    private val setDrinkName: SetDrinkName,
    private val editDrinkIngredients: EditDrinkIngredients,
    private val deleteDrinkIngredient: DeleteDrinkIngredient,
    private val editDrinkRecipe: EditDrinkRecipe,
    private val setDrinkRecipe: SetDrinkRecipe,
    private val addDrinkIngredient: AddDrinkIngredient,
    private val setDrinkIngredientAmount: SetDrinkIngredientAmount,
    private val toggleDrinkVisibility: ToggleDrinkVisibility,
    private val deleteDrink: DeleteDrink,
    private val addIngredient: AddIngredient,
    private val editIngredientName: EditIngredientName,
    private val editIngredientType: EditIngredientType,
    private val setIngredientName: SetIngredientName,
    private val setIngredientType: SetIngredientType,
    private val toggleIngredientVisibility: ToggleIngredientVisibility,
    private val searchDrinks: SearchDrinks,
    
    private val accountService: AccountService,
) {
    
    private val log = KotlinLogging.logger {}
    
    suspend fun handle(rq: TgRequest) {
        log.info { rq.update }
        val (command, _) = rq.command()
        when (command) {
            "start" -> greet(rq)
            "new" -> addDrink(rq)
            "ingredients" -> getIngredients(rq)
        }
        val (cqCommand, cqArgs) = rq.callbackQueryCommand()
        when (cqCommand) {
            "drink_edit" -> editDrink(cqArgs.elementAtOrNull(0)?.let(UUID::fromString), rq)
            "drink_edit_name" -> editDrinkName(rq)
            "drink_edit_ingredient" -> editDrinkIngredientAmount(UUID.fromString(cqArgs[0]), rq)
            "drink_edit_ingredients" -> editDrinkIngredients(rq)
            "drink_delete_ingredient" -> deleteDrinkIngredient(rq)
            "drink_edit_recipe" -> editDrinkRecipe(rq)
            "drink_add_ing" -> addDrinkIngredient(UUID.fromString(cqArgs[0]), rq)
            "drink_ing_amt" -> setDrinkIngredientAmount(Amount.parse(cqArgs[0]), rq)
            "drink_toggle_visibility" -> toggleDrinkVisibility(rq)
            "drink_delete" -> deleteDrink(rq)
            "ingredient_add" -> addIngredient(rq)
            "ingredient_edit_name" -> editIngredientName(rq)
            "ingredient_edit_type" -> editIngredientType(rq)
            "ingredient_set_type" -> setIngredientType(Ingredient.Type.valueOf(cqArgs[0]), rq)
            "ingredient_toggle_visibility" -> toggleIngredientVisibility(rq)
        }
        if (command != null || cqCommand != null) {
            return
        }
        val messageText = rq.messageText()
        if (messageText != null) {
            val account = accountService.ensureTgAccount(rq.userId())
            if (account.tgSession?.drinkEdit?.editingName == true) {
                return setDrinkName(messageText, rq)
            }
            if (account.tgSession?.drinkEdit?.editingRecipe == true) {
                return setDrinkRecipe(messageText, rq)
            }
            if (account.tgSession?.ingredientEdit?.editingName == true) {
                return setIngredientName(messageText, rq)
            }
            return searchDrinks(messageText, rq)
        }
    }
}
