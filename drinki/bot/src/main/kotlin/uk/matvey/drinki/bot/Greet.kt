package uk.matvey.drinki.bot

import com.pengrad.telegrambot.TelegramBot
import uk.matvey.telek.TgRequest
import uk.matvey.telek.TgSendMessageSupport.sendMessage

class Greet(
    private val bot: TelegramBot,
) {

    operator fun invoke(rq: TgRequest) {
        bot.sendMessage(rq.userId(), "🍹")
    }
}
