package uk.matvey.begit.athlete

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.Message
import com.pengrad.telegrambot.model.request.InlineKeyboardButton
import uk.matvey.begit.athlete.AthleteSql.getAthleteByTgChatId
import uk.matvey.begit.club.ClubMemberSql.findAllClubsByAthleteId
import uk.matvey.begit.event.EventParticipantSql.findAllEventsByAthleteId
import uk.matvey.slon.repo.Repo
import uk.matvey.telek.TgSendMessageSupport.sendMessage

class AthleteUpdateProcessor(
    private val repo: Repo,
    private val bot: TelegramBot,
) {

    suspend fun showMyClubs(message: Message) {
        val clubs = repo.access { a ->
            val athlete = a.getAthleteByTgChatId(message.from().id().toLong())
            a.findAllClubsByAthleteId(athlete.id)
        }
        bot.sendMessage(
            message.chat().id(),
            "Your clubs:",
            clubs.map { listOf(InlineKeyboardButton(it.name).callbackData("/clubs/${it.id}/show")) }
        )
    }

    suspend fun showMyEvents(message: Message) {
        val events = repo.access { a ->
            val athlete = a.getAthleteByTgChatId(message.from().id().toLong())
            a.findAllEventsByAthleteId(athlete.id)
        }
        bot.sendMessage(
            message.chat().id(),
            "Events:",
            events.map { listOf(InlineKeyboardButton(it.title).callbackData("/events/${it.id}/show")) }
        )
    }
}