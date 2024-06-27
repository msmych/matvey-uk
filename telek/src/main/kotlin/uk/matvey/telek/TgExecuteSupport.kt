package uk.matvey.telek

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.request.AnswerCallbackQuery
import com.pengrad.telegrambot.request.DeleteMessage

object TgExecuteSupport {

    fun TelegramBot.deleteMessage(chatId: Long, messageId: Int) = execute(DeleteMessage(chatId, messageId))

    fun TelegramBot.answerCallbackQuery(callbackQueryId: String) = execute(AnswerCallbackQuery(callbackQueryId))
}