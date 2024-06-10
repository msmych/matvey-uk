package uk.matvey.begit.bot

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.request.InlineKeyboardButton
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup
import com.pengrad.telegrambot.model.request.ParseMode.MarkdownV2
import com.pengrad.telegrambot.request.SendMessage
import uk.matvey.begit.club.ClubService

class ClubUpdateProcessor(
    private val clubService: ClubService,
    private val bot: TelegramBot,
) {

    fun greetClub(title: String, chatId: Long) {
        val (_, count) = clubService.ensureClub(title, chatId)
        bot.execute(
            SendMessage(
                chatId, """
                        *$title events*
                    """.trimIndent()
            )
                .replyMarkup(InlineKeyboardMarkup(InlineKeyboardButton("Join! ($count)").callbackData("/clubs/join")))
                .parseMode(MarkdownV2)
        )
    }

    fun joinClub(chatId: Long, userId: Long, username: String) {
        clubService.addClubMember(chatId, userId, username)
        bot.execute(SendMessage(chatId, "Welcome!"))
    }
}