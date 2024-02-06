package uk.matvey.drinki.bot.ingredient

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.request.SendMessage
import uk.matvey.drinki.ingredient.IngredientRepo
import uk.matvey.telek.TgRequest

class GetIngredients(
    private val ingredients: IngredientRepo,
    private val bot: TelegramBot,
) {

    operator fun invoke(rq: TgRequest) {
        val publicIngredients = ingredients.publicIngredients()
        bot.execute(SendMessage(rq.userId(), publicIngredients.joinToString("\n") { it.name }))
    }
}
