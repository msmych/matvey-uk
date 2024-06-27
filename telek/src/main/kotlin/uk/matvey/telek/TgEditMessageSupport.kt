package uk.matvey.telek

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.request.InlineKeyboardButton
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup
import com.pengrad.telegrambot.model.request.ParseMode.MarkdownV2
import com.pengrad.telegrambot.request.EditMessageReplyMarkup
import com.pengrad.telegrambot.request.EditMessageText
import com.pengrad.telegrambot.response.BaseResponse

object TgEditMessageSupport {

    fun TelegramBot.editMessage(
        chatId: Long,
        messageId: Int,
        text: String?,
        markup: List<List<InlineKeyboardButton>> = listOf(),
    ): BaseResponse {
        val newMarkup = InlineKeyboardMarkup(*markup.map { it.toTypedArray() }.toTypedArray())
        val response = execute(
            text?.let { newText ->
                EditMessageText(chatId, messageId, newText)
                    .parseMode(MarkdownV2)
                    .replyMarkup(newMarkup)
            } ?: EditMessageReplyMarkup(chatId, messageId).replyMarkup(newMarkup)
        )
        return response
    }
}