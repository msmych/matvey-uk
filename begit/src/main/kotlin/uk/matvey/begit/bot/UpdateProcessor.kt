package uk.matvey.begit.bot

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.Update
import uk.matvey.begit.club.ClubRepo
import uk.matvey.begit.club.ClubService

class UpdateProcessor(
    clubService: ClubService,
    clubRepo: ClubRepo,
    bot: TelegramBot,
) {

    private val clubUpdateProcessor = ClubUpdateProcessor(clubService, clubRepo, bot)

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
            val callbackQueryId = callbackQuery.id()
            val message = callbackQuery.maybeInaccessibleMessage()
            val chatId = message.chat().id()
            val messageId = message.messageId()
            callbackQuery.data()?.let { data ->
                val from = callbackQuery.from()
                val userId = from.id()
                val username = from.username()
                if (data == "/clubs/join") {
                    clubUpdateProcessor.joinClub(chatId, userId, username, messageId, callbackQueryId)
                }
            }
        }
    }
}