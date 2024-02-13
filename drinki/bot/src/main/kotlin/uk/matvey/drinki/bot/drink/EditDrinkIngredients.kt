package uk.matvey.drinki.bot.drink

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.request.ParseMode.MarkdownV2
import com.pengrad.telegrambot.request.EditMessageText
import uk.matvey.drinki.account.AccountRepo
import uk.matvey.drinki.bot.ingredient.IngredientTg
import uk.matvey.drinki.drink.DrinkService
import uk.matvey.drinki.ingredient.IngredientRepo
import uk.matvey.telek.TgRequest

class EditDrinkIngredients(
    private val accountRepo: AccountRepo,
    private val ingredientRepo: IngredientRepo,
    private val drinkService: DrinkService,
    private val bot: TelegramBot,
) {
    
    operator fun invoke(rq: TgRequest) {
        val account = accountRepo.getByTgUserId(rq.userId())
        val drink = drinkService.getDrinkDetails(account.tgSession().drinkEdit().drinkId)
        val publicIngredients = ingredientRepo.publicIngredients()
        bot.execute(
            EditMessageText(
                rq.userId(),
                rq.messageId(),
                DrinkTg.drinkDetailsText(drink),
            )
                .parseMode(MarkdownV2)
                .replyMarkup(
                    IngredientTg.editDrinkIngredientsKeyboard(
                        drink.ingredients.keys.associateBy { it.id },
                        publicIngredients
                    )
                )
        )
    }
}
