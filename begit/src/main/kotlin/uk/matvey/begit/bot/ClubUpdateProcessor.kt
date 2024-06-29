package uk.matvey.begit.bot

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.CallbackQuery
import com.pengrad.telegrambot.model.request.InlineKeyboardButton
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.long
import kotlinx.serialization.json.put
import uk.matvey.begit.athlete.AthleteRepo
import uk.matvey.begit.club.Club
import uk.matvey.begit.club.ClubRepo
import uk.matvey.begit.club.ClubService
import uk.matvey.telek.TgEditMessageSupport.editMessage
import uk.matvey.telek.TgExecuteSupport.deleteMessage
import uk.matvey.telek.TgSendMessageSupport.sendMessage
import java.util.UUID

class ClubUpdateProcessor(
    private val clubService: ClubService,
    private val clubRepo: ClubRepo,
    private val athleteRepo: AthleteRepo,
    private val bot: TelegramBot,
) {

    fun greetClub(title: String, chatId: Long) {
        val (club, count) = clubService.ensureClub(title, chatId)
        club.refs.tgChatMessageId?.let { (chatId, messageId) ->
            bot.deleteMessage(chatId, messageId)
        }
        val sendResponse = bot.sendMessage(
            chatId,
            """Club *$title*
                |
                |Join the club by clicking the button below
            """.trimMargin(),
            joinMarkup(club.id, count)
        )
        clubService.updateClub(club.updateTgChatMessageId(sendResponse.message().messageId()))
    }

    fun processCallback(callbackQuery: CallbackQuery) {
        val data = callbackQuery.data()
        val message = callbackQuery.maybeInaccessibleMessage()
        val messageId = message.messageId()
        val chatId = message.chat().id()
        val from = callbackQuery.from()
        val userId = from.id()
        val username = from.username()
        CLUBS_SHOW_REGEX.matchEntire(data)?.let { match ->
            val clubId = UUID.fromString(match.groupValues[1])
            val club = clubRepo.getById(clubId)
            showClub(userId, messageId, club)
            return
        }
        CLUBS_JOIN_REGEX.matchEntire(data)?.let { match ->
            val clubId = UUID.fromString(match.groupValues[1])
            joinClub(chatId, clubId, userId, username, messageId)
            return
        }
        CLUBS_LEAVE_REGEX.matchEntire(data)?.let { match ->
            val clubId = UUID.fromString(match.groupValues[1])
            leaveClub(clubId, userId)
            return
        }
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
        bot.sendMessage(
            tgUserId, "*${club.name}*",
            listOf(
                listOf(InlineKeyboardButton("New event").callbackData("/events/new?clubId=${club.id}")),
                listOf(InlineKeyboardButton("Leave club").callbackData("/clubs/${club.id}/leave")),
            )
        )
        val count = clubRepo.countClubMembers(clubId)
        bot.editMessage(chatId, messageId, null, joinMarkup(clubId, count))
    }

    private fun showClub(userId: Long, messageId: Int, club: Club) {
        bot.editMessage(
            userId,
            messageId,
            "*${club.name}*",
            listOf(
                listOf(InlineKeyboardButton("New event").callbackData("/events/new?clubId=${club.id}")),
                listOf(InlineKeyboardButton("Leave club").callbackData("/clubs/${club.id}/leave")),
            )
        )
    }

    private fun leaveClub(clubId: UUID, tgUserId: Long) {
        val athlete = athleteRepo.getByTgChatId(tgUserId)
        clubRepo.findClubMemberRefs(clubId, athlete.id)?.let { clubMemberRefs ->
            val removed = clubService.removeClubMember(clubId, athlete.id)
            if (!removed) {
                return
            }
            val club = clubRepo.getById(clubId)
            bot.sendMessage(tgUserId, "You have left ${club.name}")
            val count = clubRepo.countClubMembers(clubId)
            bot.editMessage(
                clubMemberRefs.getValue("tgChatId").jsonPrimitive.long,
                clubMemberRefs.getValue("tgMessageId").jsonPrimitive.int,
                null,
                joinMarkup(clubId, count)
            )
        }
    }

    private fun joinMarkup(clubId: UUID, count: Int): List<List<InlineKeyboardButton>> {
        return listOf(listOf(InlineKeyboardButton("Join club! ($count)").callbackData("/clubs/$clubId/join")))
    }

    companion object {

        val UUID_REGEX = "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}".toRegex()
        private val CLUBS_JOIN_REGEX = "/clubs/($UUID_REGEX)/join".toRegex()
        private val CLUBS_SHOW_REGEX = "/clubs/($UUID_REGEX)/show".toRegex()
        private val CLUBS_LEAVE_REGEX = "/clubs/($UUID_REGEX)/leave".toRegex()
    }
}