package uk.matvey.begit.bot

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.Update
import uk.matvey.begit.club.ClubService

class UpdateProcessor(
    clubService: ClubService,
    bot: TelegramBot,
) {

    private val clubUpdateProcessor = ClubUpdateProcessor(clubService, bot)

    fun process(update: Update) {
        update.message()?.let { message ->
            val chat = message.chat()
            val chatId = chat.id()
            val title = chat.title()
            message.text()?.let { text ->
                if (text.startsWith("/club") && title != null) {
                    clubUpdateProcessor.greetClub(title, chatId)
                }
            }
        }
        update.callbackQuery()?.let { callbackQuery ->
            callbackQuery.data()?.let { data ->
                val chatId = callbackQuery.maybeInaccessibleMessage().chat().id()
                val from = callbackQuery.from()
                val userId = from.id()
                val username = from.username()
                if (data == "/clubs/join") {
                    clubUpdateProcessor.joinClub(chatId, userId, username)
                }
            }
        }
    }
}