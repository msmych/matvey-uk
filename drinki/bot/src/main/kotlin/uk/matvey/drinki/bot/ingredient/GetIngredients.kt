package uk.matvey.drinki.bot.ingredient

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.request.InlineKeyboardButton
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup
import com.pengrad.telegrambot.request.SendMessage
import uk.matvey.drinki.ingredient.IngredientRepo
import uk.matvey.telek.TgRequest

class GetIngredients(
    private val ingredients: IngredientRepo,
    private val bot: TelegramBot,
) {

    operator fun invoke(rq: TgRequest) {
        val publicIngredients = ingredients.publicIngredients()
        val keyboard = InlineKeyboardMarkup(
            InlineKeyboardButton("New ingredient").callbackData("/ingredient_add")
        )
        bot.execute(
            SendMessage(rq.userId(), publicIngredients.joinToString("\n") { "* ${it.name}" })
                .replyMarkup(keyboard)
        )
    }
}
