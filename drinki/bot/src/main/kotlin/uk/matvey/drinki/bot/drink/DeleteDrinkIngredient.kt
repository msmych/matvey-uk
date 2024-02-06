package uk.matvey.drinki.bot.drink

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.request.ParseMode.MarkdownV2
import com.pengrad.telegrambot.request.EditMessageText
import uk.matvey.drinki.account.AccountRepo
import uk.matvey.drinki.drink.DrinkRepo
import uk.matvey.drinki.ingredient.IngredientRepo
import uk.matvey.drinki.bot.ingredient.IngredientTg
import uk.matvey.telek.TgRequest

class DeleteDrinkIngredient(
    private val accountRepo: AccountRepo,
    private val drinkRepo: DrinkRepo,
    private val ingredientRepo: IngredientRepo,
    private val bot: TelegramBot,
) {

    operator fun invoke(rq: TgRequest) {
        val account = accountRepo.getByTgUserId(rq.userId())
        val drink = drinkRepo.get(account.tgSession().drinkEdit().drinkId)
            .deleteIngredient(account.tgSession().drinkEdit().ingredientId())
        drinkRepo.update(drink)
        val drinkIngredients = ingredientRepo.findAllByDrink(drink.id)
        val publicIngredients = ingredientRepo.publicIngredients()
        bot.execute(
            EditMessageText(
                rq.userId(),
                rq.messageId(),
                DrinkTg.drinkTitle(drink)
            )
                .parseMode(MarkdownV2)
                .replyMarkup(
                    IngredientTg.editIngredientsKeyboard(
                        drink,
                        drinkIngredients.associateBy { it.id },
                        publicIngredients
                    )
                )
        )
    }
}
