package uk.matvey.drinki.bot.drink

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.request.ParseMode.MarkdownV2
import com.pengrad.telegrambot.request.EditMessageText
import uk.matvey.drinki.account.AccountRepo
import uk.matvey.drinki.drink.DrinkRepo
import uk.matvey.drinki.ingredient.IngredientRepo
import uk.matvey.drinki.types.Amount
import uk.matvey.drinki.bot.amount.AmountTg
import uk.matvey.telek.TgRequest
import java.util.*

class AddDrinkIngredient(
    private val accountRepo: AccountRepo,
    private val drinkRepo: DrinkRepo,
    private val ingredientRepo: IngredientRepo,
    private val bot: TelegramBot,
) {

    operator fun invoke(ingredientId: UUID, rq: TgRequest) {
        val account = accountRepo.getByTgUserId(rq.userId())
        val drink = drinkRepo.get(account.tgSession().drinkEdit().drinkId)
            .setIngredient(ingredientId, Amount.Some)
        drinkRepo.update(drink)
        accountRepo.update(account.updateTgEditDrinkIngredient(ingredientId))
        val ingredients = ingredientRepo.findAllByDrink(drink.id)
        val currentIngredientText = ingredients.first { it.id == ingredientId }.name
        val messageText = """
            ${DrinkTg.drinkTitle(drink)}
            
            $currentIngredientText:
        """.trimIndent()
        bot.execute(
            EditMessageText(
                rq.userId(),
                rq.messageId(),
                messageText
            )
                .parseMode(MarkdownV2)
                .replyMarkup(AmountTg.amountsKeyboard())
        )
    }
}
