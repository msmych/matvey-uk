package uk.matvey.drinki.bot.ingredient

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.request.InlineKeyboardButton
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup
import com.pengrad.telegrambot.request.EditMessageText
import uk.matvey.drinki.account.AccountRepo
import uk.matvey.drinki.ingredient.Ingredient
import uk.matvey.drinki.ingredient.IngredientRepo
import uk.matvey.telek.TgRequest

class AddIngredient(
    private val accountRepo: AccountRepo,
    private val ingredientRepo: IngredientRepo,
    private val bot: TelegramBot,
) {

    operator fun invoke(rq: TgRequest) {
        val account = accountRepo.getByTgUserId(rq.userId())
        val ingredient = Ingredient.private(account.id, "New ingredient")
        ingredientRepo.add(ingredient)
        accountRepo.update(account.editingIngredient(ingredient.id, rq.messageId()))
        val keyboard = InlineKeyboardMarkup(
            arrayOf(InlineKeyboardButton("Name").callbackData("/ingredient_edit_name"))
        )
        bot.execute(
            EditMessageText(rq.userId(), rq.messageId(), ingredient.name)
                .replyMarkup(keyboard)
        )
    }
}