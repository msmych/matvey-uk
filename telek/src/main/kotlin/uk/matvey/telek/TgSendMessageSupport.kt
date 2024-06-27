package uk.matvey.telek

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.request.InlineKeyboardButton
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup
import com.pengrad.telegrambot.model.request.ParseMode.MarkdownV2
import com.pengrad.telegrambot.request.SendMessage
import com.pengrad.telegrambot.response.SendResponse

object TgSendMessageSupport {

    fun TelegramBot.sendMessage(
        chatId: Long,
        text: String,
        markup: List<List<InlineKeyboardButton>> = listOf(),
    ): SendResponse {
        return execute(
            SendMessage(chatId, text)
                .parseMode(MarkdownV2)
                .replyMarkup(
                    markup.takeIf { it.isNotEmpty() }?.let {
                        InlineKeyboardMarkup(*markup.map { it.toTypedArray() }.toTypedArray())
                    }
                )
        )
    }
}