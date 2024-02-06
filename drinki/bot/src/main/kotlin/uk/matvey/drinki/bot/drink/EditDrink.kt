package uk.matvey.drinki.bot.drink

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.request.ParseMode.MarkdownV2
import com.pengrad.telegrambot.request.DeleteMessage
import com.pengrad.telegrambot.request.EditMessageText
import uk.matvey.drinki.account.AccountRepo
import uk.matvey.drinki.drink.DrinkRepo
import uk.matvey.drinki.ingredient.IngredientRepo
import uk.matvey.telek.TgRequest
import java.util.*

class EditDrink(
    private val accountRepo: AccountRepo,
    private val drinkRepo: DrinkRepo,
    private val ingredientRepo: IngredientRepo,
    private val bot: TelegramBot,
) {

    operator fun invoke(drinkId: UUID?, rq: TgRequest) {
        val account = accountRepo.getByTgUserId(rq.userId())
        val drink = drinkRepo.get(drinkId ?: account.tgSession().drinkEdit().drinkId)
        val ingredients = ingredientRepo.findAllByDrink(drink.id)
        if (drinkId != null) {
            account.tgSession().drinkEdit().let { (_, messageId) ->
                bot.execute(DeleteMessage(rq.userId(), messageId))
            }
        }
        bot.execute(
            EditMessageText(rq.userId(), rq.messageId(), DrinkTg.drinkDetailsText(drink, ingredients))
                .parseMode(MarkdownV2)
                .replyMarkup(DrinkTg.drinkActionsKeyboard(drink))
        )
        accountRepo.update(account.editingDrink(drink.id, rq.messageId()))
    }
}
