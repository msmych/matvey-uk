package uk.matvey.drinki.bot.ingredient

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.request.InlineKeyboardButton
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup
import com.pengrad.telegrambot.request.SendMessage
import uk.matvey.drinki.account.AccountRepo
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
        val keyboard = InlineKeyboardMarkup(
            arrayOf(InlineKeyboardButton("Name").callbackData("/ingredient_edit_name"))
        )
        val sendRs = bot.execute(
            SendMessage(rq.userId(), ingredient.name)
                .replyMarkup(keyboard)
        )
        accountRepo.update(account.editingIngredient(ingredient.id, sendRs.message().messageId()))
    }
}
