package uk.matvey.begit.bot

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.Update
import com.pengrad.telegrambot.model.request.InlineKeyboardButton
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup
import com.pengrad.telegrambot.model.request.ParseMode.MarkdownV2
import com.pengrad.telegrambot.request.AnswerCallbackQuery
import com.pengrad.telegrambot.request.EditMessageReplyMarkup
import com.pengrad.telegrambot.request.SendMessage
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.long
import kotlinx.serialization.json.put
import uk.matvey.begit.club.ClubRepo
import uk.matvey.begit.club.ClubService
import uk.matvey.begit.member.MemberRepo
import java.util.UUID

class ClubUpdateProcessor(
    private val clubService: ClubService,
    private val clubRepo: ClubRepo,
    private val memberRepo: MemberRepo,
    private val bot: TelegramBot,
) {

    fun greetClub(title: String, chatId: Long) {
        val (club, count) = clubService.ensureClub(title, chatId)
        bot.execute(
            SendMessage(chatId, "Club *$title*")
                .replyMarkup(joinMarkup(club.id, count))
                .parseMode(MarkdownV2)
        )
    }

    fun processCallback(update: Update) {
        val callbackQuery = update.callbackQuery()
        val callbackQueryId = callbackQuery.id()
        val data = callbackQuery.data()
        val message = callbackQuery.maybeInaccessibleMessage()
        val messageId = message.messageId()
        val chatId = message.chat().id()
        val from = callbackQuery.from()
        val userId = from.id()
        val username = from.username()
        CLUBS_JOIN_REGEX.matchEntire(data)?.let { match ->
            val clubId = UUID.fromString(match.groupValues[1])
            joinClub(chatId, clubId, userId, username, messageId)
        }
        CLUBS_QUIT_REGEX.matchEntire(data)?.let { match ->
            val clubId = UUID.fromString(match.groupValues[1])
            quitClub(clubId, userId)
        }
        bot.execute(AnswerCallbackQuery(callbackQueryId))
    }

    private fun joinClub(
        chatId: Long,
        clubId: UUID,
        tgUserId: Long,
        username: String,
        messageId: Int
    ) {
        val club = clubRepo.getById(clubId)
        val clubMemberRefs = buildJsonObject {
            put("tgChatId", chatId)
            put("tgMessageId", messageId)
        }
        val added = clubService.addClubMember(clubId, tgUserId, username, clubMemberRefs)
        if (!added) {
            return
        }
        bot.execute(
            SendMessage(tgUserId, "Welcome to ${club.name}!")
                .replyMarkup(InlineKeyboardMarkup(InlineKeyboardButton("Quit").callbackData("/clubs/$clubId/quit")))
        )
        val count = clubRepo.countClubMembers(clubId)
        bot.execute(
            EditMessageReplyMarkup(chatId, messageId)
                .replyMarkup(joinMarkup(clubId, count))
        )
    }

    private fun quitClub(clubId: UUID, tgUserId: Long) {
        val member = memberRepo.getByTgId(tgUserId)
        clubRepo.findClubMemberRefs(clubId, member.id)?.let { clubMemberRefs ->
            val removed = clubService.removeClubMember(clubId, member.id)
            if (!removed) {
                return
            }
            val club = clubRepo.getById(clubId)
            bot.execute(
                SendMessage(tgUserId, "You have left ${club.name}")
            )
            val count = clubRepo.countClubMembers(clubId)
            bot.execute(
                EditMessageReplyMarkup(
                    clubMemberRefs.getValue("tgChatId").jsonPrimitive.long,
                    clubMemberRefs.getValue("tgMessageId").jsonPrimitive.int
                )
                    .replyMarkup(joinMarkup(clubId, count))
            )
        }
    }

    private fun joinMarkup(clubId: UUID, count: Int): InlineKeyboardMarkup {
        return InlineKeyboardMarkup(InlineKeyboardButton("Join! ($count)").callbackData("/clubs/$clubId/join"))
    }

    companion object {

        private val UUID_REGEX = "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}".toRegex()
        private val CLUBS_JOIN_REGEX = "/clubs/($UUID_REGEX)/join".toRegex()
        private val CLUBS_QUIT_REGEX = "/clubs/($UUID_REGEX)/quit".toRegex()
    }
}