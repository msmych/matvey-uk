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
import uk.matvey.drinki.bot.drink.DrinkTgService
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
import uk.matvey.drinki.drink.DrinkRepo
import uk.matvey.drinki.drink.DrinkService
import uk.matvey.drinki.ingredient.IngredientRepo
import uk.matvey.telek.TgRequest

private val log = KotlinLogging.logger("drinki-bot")

fun startBot(
    config: Config,
    accountRepo: AccountRepo,
    accountService: AccountService,
    drinkRepo: DrinkRepo,
    ingredientRepo: IngredientRepo,
    drinkService: DrinkService,
) {
    val bot = TelegramBot(config.getString("bot.token"))
    
    val drinkTgService = DrinkTgService(drinkService, bot)
    
    val botUpdateHandler = BotUpdateHandler(
        Greet(bot),
        AddDrink(accountService, accountRepo, drinkRepo, bot),
        GetIngredients(ingredientRepo, bot),
        EditDrink(accountRepo, drinkService, bot),
        EditDrinkName(accountRepo, drinkService, bot),
        EditDrinkIngredientAmount(accountRepo, drinkTgService),
        SetDrinkName(accountRepo, drinkRepo, drinkService, bot),
        EditDrinkIngredients(accountRepo, ingredientRepo, drinkService, bot),
        DeleteDrinkIngredient(accountRepo, drinkRepo, ingredientRepo, drinkService, bot),
        EditDrinkRecipe(accountRepo, drinkService, bot),
        SetDrinkRecipe(accountRepo, drinkRepo, drinkService, bot),
        AddDrinkIngredient(accountRepo, drinkRepo, drinkTgService),
        SetDrinkIngredientAmount(accountRepo, drinkRepo, ingredientRepo, drinkService, bot),
        ToggleDrinkVisibility(accountRepo, drinkRepo, drinkService, bot),
        DeleteDrink(accountRepo, drinkRepo, bot),
        AddIngredient(accountService, accountRepo, ingredientRepo, bot),
        EditIngredientName(accountRepo, ingredientRepo, bot),
        EditIngredientType(accountRepo, ingredientRepo, bot),
        SetIngredientName(accountRepo, ingredientRepo, bot),
        SetIngredientType(accountRepo, ingredientRepo, bot),
        ToggleIngredientVisibility(accountRepo, ingredientRepo, bot),
        SearchDrinks(accountRepo, drinkRepo, drinkService, bot),
        
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
