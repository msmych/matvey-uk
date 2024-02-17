package uk.matvey.drinki.bot

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.request.SendMessage
import uk.matvey.telek.TgRequest

class Greet(
    private val bot: TelegramBot,
) {
    
    operator fun invoke(rq: TgRequest) {
        bot.execute(SendMessage(rq.userId(), "🍹"))
    }
}
