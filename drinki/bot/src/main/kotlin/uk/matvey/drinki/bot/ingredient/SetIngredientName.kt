package uk.matvey.drinki.bot.ingredient

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.request.DeleteMessage
import com.pengrad.telegrambot.request.SendMessage
import uk.matvey.drinki.account.AccountRepo
import uk.matvey.drinki.bot.ingredient.IngredientTg.ingredientActionsKeyboard
import uk.matvey.drinki.ingredient.IngredientRepo
import uk.matvey.telek.TgRequest

class SetIngredientName(
    private val accountRepo: AccountRepo,
    private val ingredientRepo: IngredientRepo,
    private val bot: TelegramBot,
) {

    operator fun invoke(name: String, rq: TgRequest) {
        val account = accountRepo.getByTgUserId(rq.userId())
        val ingredient = ingredientRepo.get(account.tgSession().ingredientEdit().ingredientId)
            .setName(name)
        ingredientRepo.update(ingredient)
        bot.execute(DeleteMessage(rq.userId(), account.tgSession().ingredientEdit().messageId))
        val sendRs = bot.execute(
            SendMessage(rq.userId(), ingredient.name)
                .replyMarkup(ingredientActionsKeyboard(ingredient))
        )
        accountRepo.update(account.editingIngredient(ingredient.id, sendRs.message().messageId()))
    }
}
