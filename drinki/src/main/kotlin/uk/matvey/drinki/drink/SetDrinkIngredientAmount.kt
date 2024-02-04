package uk.matvey.drinki.drink

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.request.ParseMode.MarkdownV2
import com.pengrad.telegrambot.request.EditMessageText
import uk.matvey.drinki.account.AccountRepo
import uk.matvey.drinki.ingredient.IngredientRepo
import uk.matvey.drinki.ingredient.IngredientTg
import uk.matvey.drinki.types.Amount
import uk.matvey.telek.TgRequest

class SetDrinkIngredientAmount(
    private val accountRepo: AccountRepo,
    private val drinkRepo: DrinkRepo,
    private val ingredientRepo: IngredientRepo,
    private val bot: TelegramBot,
) {

    operator fun invoke(amount: Amount, rq: TgRequest) {
        val account = accountRepo.getByTgUserId(rq.userId())
        val drinkEdit = account.tgSession().drinkEdit()
        val drink = drinkRepo.get(drinkEdit.drinkId)
            .setIngredient(drinkEdit.ingredientId(), amount)
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