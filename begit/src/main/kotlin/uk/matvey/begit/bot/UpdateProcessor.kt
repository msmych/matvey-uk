package uk.matvey.begit.bot

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.Update
import uk.matvey.begit.event.EventUpdateProcessor
import uk.matvey.begit.tg.TgSessionSql.findTgSessionByChatId
import uk.matvey.slon.Repo
import uk.matvey.telek.TgExecuteSupport.answerCallbackQuery

class UpdateProcessor(
    private val repo: Repo,
    private val eventUpdateProcessor: EventUpdateProcessor,
    private val clubUpdateProcessor: ClubUpdateProcessor,
    private val bot: TelegramBot,
) {

    fun process(update: Update) {
        val message = update.message()
        if (message != null) {
            val chat = message.chat()
            val chatId = chat.id()
            val title = chat.title()
            val text = message.text()
            val fromId = message.from().id()
            if (fromId == chatId) {
                repo.access { a -> a.findTgSessionByChatId(chatId) }?.let { tgSession ->
                    if (tgSession.data.awaitingAnswer != null) {
                        eventUpdateProcessor.processAnswer(message, tgSession)
                        return
                    }
                }
            }
            when {
                text.startsWith("/club") && title != null -> clubUpdateProcessor.greetClub(title, chatId)
            }
            return
        }
        val callbackQuery = update.callbackQuery()
        if (callbackQuery != null) {
            val data = callbackQuery.data()
            when {
                data.startsWith("/clubs") -> clubUpdateProcessor.processCallback(callbackQuery)
                data.startsWith("/events") -> eventUpdateProcessor.processCallback(callbackQuery)
            }
            bot.answerCallbackQuery(callbackQuery.id())
            return
        }
    }
}