package uk.matvey.drinki.bot

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.UpdatesListener.CONFIRMED_UPDATES_ALL
import com.pengrad.telegrambot.request.AnswerCallbackQuery
import com.typesafe.config.Config
import mu.KotlinLogging
import uk.matvey.drinki.account.AccountRepo
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
import uk.matvey.drinki.drink.DrinkRepo
import uk.matvey.drinki.ingredient.IngredientRepo
import uk.matvey.telek.TgRequest

private val log = KotlinLogging.logger("drinki-bot")

fun startBot(
    config: Config,
    accountRepo: AccountRepo,
    accountService: AccountService,
    drinkRepo: DrinkRepo,
    ingredientRepo: IngredientRepo
) {
    val bot = TelegramBot(config.getString("bot.token"))
    val botUpdateHandler = BotUpdateHandler(
        Greet(bot),
        AddDrink(accountService, accountRepo, drinkRepo, bot),
        EditDrink(accountRepo, drinkRepo, ingredientRepo, bot),
        EditDrinkName(accountRepo, drinkRepo, bot),
        EditDrinkIngredientAmount(accountRepo, drinkRepo, ingredientRepo, bot),
        SetDrinkName(accountRepo, drinkRepo, ingredientRepo, bot),
        EditDrinkIngredients(accountRepo, drinkRepo, ingredientRepo, bot),
        DeleteDrinkIngredient(accountRepo, drinkRepo, ingredientRepo, bot),
        EditDrinkRecipe(accountRepo, drinkRepo, bot),
        SetDrinkRecipe(accountRepo, drinkRepo, ingredientRepo, bot),
        AddDrinkIngredient(accountRepo, drinkRepo, ingredientRepo, bot),
        SetDrinkIngredientAmount(accountRepo, drinkRepo, ingredientRepo, bot),
        ToggleDrinkVisibility(accountRepo, drinkRepo, ingredientRepo, bot),
        DeleteDrink(accountRepo, drinkRepo, bot),
        SearchDrinks(accountRepo, drinkRepo, ingredientRepo, bot),

        accountService,
    )
    bot.setUpdatesListener { updates ->
        updates.forEach { update ->
            try {
                val rq = TgRequest(update)
                botUpdateHandler.handle(rq)
                if (rq.isCallbackQuery()) {
                    bot.execute(AnswerCallbackQuery(rq.callbackQueryId()))
                }
            } catch (e: Exception) {
                log.error(e) { "Oops" }
            }
        }
        CONFIRMED_UPDATES_ALL
    }
}
