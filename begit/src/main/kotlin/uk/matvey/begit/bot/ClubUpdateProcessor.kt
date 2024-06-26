package uk.matvey.begit.bot

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.request.InlineKeyboardButton
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup
import com.pengrad.telegrambot.model.request.ParseMode.MarkdownV2
import com.pengrad.telegrambot.request.AnswerCallbackQuery
import com.pengrad.telegrambot.request.EditMessageReplyMarkup
import com.pengrad.telegrambot.request.SendMessage
import uk.matvey.begit.club.ClubRepo
import uk.matvey.begit.club.ClubService

class ClubUpdateProcessor(
    private val clubService: ClubService,
    private val clubRepo: ClubRepo,
    private val bot: TelegramBot,
) {

    fun greetClub(title: String, chatId: Long) {
        val (_, count) = clubService.ensureClub(title, chatId)
        bot.execute(
            SendMessage(chatId, "*$title events*")
                .replyMarkup(joinMarkup(count))
                .parseMode(MarkdownV2)
        )
    }

    fun joinClub(
        chatId: Long,
        userId: Long,
        username: String,
        messageId: Int,
        callbackQueryId: String
    ) {
        val club = clubRepo.getClubByTgId(chatId)
        val added = clubService.addClubMember(club.id, userId, username)
        bot.execute(AnswerCallbackQuery(callbackQueryId))
        if (!added) {
            return
        }
        bot.execute(SendMessage(chatId, "Welcome!"))
        val count = clubRepo.countClubMembers(club.id)
        bot.execute(
            EditMessageReplyMarkup(chatId, messageId)
                .replyMarkup(joinMarkup(count))
        )
    }

    private fun joinMarkup(count: Int): InlineKeyboardMarkup {
        return InlineKeyboardMarkup(InlineKeyboardButton("Join! ($count)").callbackData("/clubs/join"))
    }
}