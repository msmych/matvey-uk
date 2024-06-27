package uk.matvey.begit.event

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.CallbackQuery
import com.pengrad.telegrambot.model.Message
import com.pengrad.telegrambot.model.request.InlineKeyboardButton
import uk.matvey.begit.athlete.Athlete
import uk.matvey.begit.athlete.AthleteSql.getAthleteByTgChatId
import uk.matvey.begit.bot.ClubUpdateProcessor.Companion.UUID_REGEX
import uk.matvey.begit.club.Club
import uk.matvey.begit.club.ClubSql.getClubById
import uk.matvey.begit.event.EventParticipantSql.addEventParticipant
import uk.matvey.begit.event.EventParticipantSql.countEventParticipants
import uk.matvey.begit.event.EventSql.getEventById
import uk.matvey.begit.event.EventSql.insertEvent
import uk.matvey.begit.event.EventSql.updateEvent
import uk.matvey.begit.tg.TgSession
import uk.matvey.begit.tg.TgSession.Data.AwaitingAnswer.EVENT_DESCRIPTION
import uk.matvey.begit.tg.TgSession.Data.AwaitingAnswer.EVENT_TITLE
import uk.matvey.begit.tg.TgSessionSql.ensureTgSession
import uk.matvey.begit.tg.TgSessionSql.getTgSessionByChatId
import uk.matvey.begit.tg.TgSessionSql.updateTgSession
import uk.matvey.slon.Repo
import uk.matvey.telek.TgEditMessageSupport.editMessage
import uk.matvey.telek.TgExecuteSupport.deleteMessage
import uk.matvey.telek.TgSendMessageSupport.sendMessage
import uk.matvey.telek.TgSupport.tgEscape
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.YearMonth
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.UUID

class EventUpdateProcessor(
    private val repo: Repo,
    private val eventRepo: EventRepo,
    private val bot: TelegramBot,
) {
    fun processCallback(callbackQuery: CallbackQuery) {
        val data = callbackQuery.data()
        val tgFromId = callbackQuery.from().id()
        val messageId = callbackQuery.maybeInaccessibleMessage().messageId()
        EVENTS_NEW_REGEX.matchEntire(data)?.let { match ->
            val clubId = UUID.fromString(match.groupValues[1])
            createEvent(clubId, tgFromId, messageId)
            return
        }
        EVENTS_SHOW.matchEntire(data)?.let { match ->
            val eventId = UUID.fromString(match.groupValues[1])
            val event = eventRepo.getById(eventId)
            showEvent(tgFromId, messageId, event)
            return
        }
        EVENTS_EDIT_DATE_REGEX.matchEntire(data)?.let { match ->
            val eventId = UUID.fromString(match.groupValues[1])
            askMonth(eventId, tgFromId, messageId)
            return
        }
        EVENTS_SET_MONTH_REGEX.matchEntire(data)?.let { match ->
            val eventId = UUID.fromString(match.groupValues[1])
            val month = YearMonth.parse(match.groupValues[2])
            setMonthAskDay(eventId, month, tgFromId, messageId)
            return
        }
        EVENTS_SET_DATE_REGEX.matchEntire(data)?.let { match ->
            val eventId = UUID.fromString(match.groupValues[1])
            val date = LocalDate.parse(match.groupValues[2])
            setDate(eventId, date, tgFromId, messageId)
            return
        }
        EVENTS_EDIT_TIME_REGEX.matchEntire(data)?.let { match ->
            val eventId = UUID.fromString(match.groupValues[1])
            askPartOfDay(eventId, tgFromId, messageId)
            return
        }
        EVENTS_SET_PART_OF_DAY_REGEX.matchEntire(data)?.let { match ->
            val eventId = UUID.fromString(match.groupValues[1])
            val partOfDay = LocalTime.of(match.groupValues[2].toInt(), 0)
            setPartOfDayAskTime(eventId, partOfDay, tgFromId, messageId)
            return
        }
        EVENTS_SET_TIME_REGEX.matchEntire(data)?.let { match ->
            val eventId = UUID.fromString(match.groupValues[1])
            val time = LocalTime.of(match.groupValues[2].toInt(), match.groupValues[3].toInt())
            setTime(eventId, time, tgFromId, messageId)
            return
        }
        EVENTS_EDIT_TITLE_REGEX.matchEntire(data)?.let { match ->
            val eventId = UUID.fromString(match.groupValues[1])
            askTitle(eventId, tgFromId, messageId)
            return
        }
        EVENTS_EDIT_DESCRIPTION_REGEX.matchEntire(data)?.let { match ->
            val eventId = UUID.fromString(match.groupValues[1])
            askDescription(eventId, tgFromId, messageId)
            return
        }
        EVENTS_PUBLISH_REGEX.matchEntire(data)?.let { match ->
            val eventId = UUID.fromString(match.groupValues[1])
            publish(eventId, tgFromId, messageId)
            return
        }
        EVENTS_JOIN_REGEX.matchEntire(data)?.let { match ->
            val eventId = UUID.fromString(match.groupValues[1])
            joinEvent(eventId, tgFromId)
            return
        }
    }

    fun processAnswer(message: Message, tgSession: TgSession) {
        val text = message.text()
        val messageId = message.messageId()
        when (tgSession.data.awaitingAnswer) {
            EVENT_TITLE -> setTitle(text, tgSession, messageId)
            EVENT_DESCRIPTION -> setDescription(text, tgSession, messageId)
            else -> error("Unexpected awaiting answer: ${tgSession.data.awaitingAnswer}")
        }
    }

    private fun createEvent(clubId: UUID, tgFromId: Long, messageId: Int) {
        val event = repo.access { a ->
            val athlete = a.getAthleteByTgChatId(tgFromId)
            val tgSession = a.ensureTgSession(tgFromId)
                .updateEditEventMessageId(tgFromId, messageId)
            a.updateTgSession(tgSession)
            val event = a.insertEvent(clubId, athlete.id)
            a.addEventParticipant(event.id, athlete.id)
            event
        }
        showEvent(tgFromId, messageId, event)
    }

    private fun showEvent(tgFromId: Long, messageId: Int, event: Event) {
        bot.editMessage(
            tgFromId,
            messageId,
            eventStr(event),
            eventActionsMarkup(event)
        )
    }

    private fun askMonth(eventId: UUID, tgFromId: Long, messageId: Int) {
        val event = eventRepo.getById(eventId)
        bot.editMessage(
            tgFromId, messageId,
            """${eventStr(event)}
            |
            |Event month:""".trimMargin(),
            monthPicker(event),
        )
    }

    private fun setMonthAskDay(eventId: UUID, month: YearMonth, tgFromId: Long, messageId: Int) {
        val event = eventRepo.getById(eventId)
        bot.editMessage(
            tgFromId,
            messageId,
            """${eventStr(event)}
                |
                |${MONTH_FORMAT.format(month)}
                |Event day:
            """.trimMargin(),
            dayPicker(event, month)
        )
    }

    private fun setDate(eventId: UUID, date: LocalDate, tgFromId: Long, messageId: Int) {
        val event = repo.access { a ->
            val event = a.getEventById(eventId)
                .updateDate(date)
            a.updateEvent(event)
            event
        }
        showEvent(tgFromId, messageId, event)
    }

    private fun askPartOfDay(eventId: UUID, tgFromId: Long, messageId: Int) {
        val event = eventRepo.getById(eventId)
        bot.editMessage(
            tgFromId,
            messageId,
            """${eventStr(event)}
                |
                |Event start time:
            """.trimMargin(),
            partOfDayPicker(event),
        )
    }

    private fun setPartOfDayAskTime(eventId: UUID, partOfDay: LocalTime, tgFromId: Long, messageId: Int) {
        val event = eventRepo.getById(eventId)
        bot.editMessage(
            tgFromId,
            messageId,
            """${eventStr(event)}
                |
                |Event start time:
            """.trimMargin(),
            timePicker(event, partOfDay),
        )
    }

    private fun setTime(eventId: UUID, time: LocalTime, tgFromId: Long, messageId: Int) {
        val event = repo.access { a ->
            val event = a.getEventById(eventId).run {
                updateTime(date().atTime(time).toInstant(ZoneOffset.UTC))
            }
            a.updateEvent(event)
            event
        }
        showEvent(tgFromId, messageId, event)
    }

    private fun askTitle(eventId: UUID, tgFromId: Long, messageId: Int) {
        val event = repo.access { a ->
            val tgSession = a.getTgSessionByChatId(tgFromId)
                .updateAwaitingAnswer(eventId, EVENT_TITLE)
            a.updateTgSession(tgSession)
            a.getEventById(eventId)
        }
        bot.editMessage(
            tgFromId,
            messageId,
            """${eventStr(event)}
                |
                |Event name:
            """.trimMargin(),
        )
    }

    private fun setTitle(title: String, tgSession: TgSession, answerMessageId: Int) {
        val eventId = tgSession.data.eventId()
        val event = repo.access { a ->
            val event = a.getEventById(eventId)
                .updateTitle(title)
            a.updateEvent(event)
            a.updateTgSession(tgSession.updateAwaitingAnswer(eventId, null))
            event
        }
        val (chatId, messageId) = tgSession.data.chatMessageId()
        showEvent(chatId, messageId, event)
        bot.deleteMessage(chatId, answerMessageId)
    }

    private fun askDescription(eventId: UUID, tgFromId: Long, messageId: Int) {
        val event = repo.access { a ->
            val tgSession = a.getTgSessionByChatId(tgFromId)
                .updateAwaitingAnswer(eventId, EVENT_DESCRIPTION)
            a.updateTgSession(tgSession)
            a.getEventById(eventId)
        }
        bot.editMessage(
            tgFromId,
            messageId,
            """${eventStr(event)}
                |
                |Event description:
            """.trimMargin(),
        )
    }

    private fun setDescription(description: String, tgSession: TgSession, answerMessageId: Int) {
        val eventId = tgSession.data.eventId()
        val event = repo.access { a ->
            val event = a.getEventById(eventId)
                .updateDescription(description)
            a.updateEvent(event)
            a.updateTgSession(tgSession.updateAwaitingAnswer(eventId, null))
            event
        }
        val (chatId, messageId) = tgSession.data.chatMessageId()
        showEvent(chatId, messageId, event)
        bot.deleteMessage(chatId, answerMessageId)
    }

    private fun publish(eventId: UUID, tgFromId: Long, messageId: Int) {
        data class Eco(val event: Event, val club: Club, val athlete: Athlete, val count: Int)
        val (event, club, organizer, count) = repo.access { a ->
            val event = a.getEventById(eventId)
            val club = a.getClubById(event.clubId)
            val organizer = a.getAthleteByTgChatId(tgFromId)
            val count = a.countEventParticipants(eventId)
            Eco(event, club, organizer, count)
        }
        val eventStr = eventStr(event)
        val sendResponse = bot.sendMessage(
            club.refs.tgChatId,
            "@${organizer.name} announced $eventStr",
            joinEventMarkup(event, count)
        )
        repo.access { a ->
            a.updateEvent(event.updateTgChatMessageId(club.refs.tgChatId, sendResponse.message().messageId()))
        }
        bot.editMessage(
            tgFromId,
            messageId,
            """$eventStr
            |
            |✅ Published
        """.trimMargin(),
            eventActionsMarkup(event)
        )
    }

    private fun joinEventMarkup(event: Event, count: Int): List<List<InlineKeyboardButton>> {
        return listOf(listOf(InlineKeyboardButton("Join event! ($count)").callbackData("/events/${event.id}/join")))
    }

    private fun joinEvent(eventId: UUID, tgFromId: Long) {
        val (event, count) = repo.access { a ->
            val athlete = a.getAthleteByTgChatId(tgFromId)
            a.addEventParticipant(eventId, athlete.id)
            val event = a.getEventById(eventId)
            val count = a.countEventParticipants(eventId)
            event to count
        }
        val (chatId, messageId) = event.refs.tgChatMessageId()
        bot.editMessage(
            chatId,
            messageId,
            null,
            joinEventMarkup(event, count)
        )
    }

    private fun eventStr(event: Event): String {
        val title = "*${event.title.tgEscape()}*"
        val dateTime =
            event.dateTime?.let { "\n\n${DATE_TIME_FORMAT.format(LocalDateTime.ofInstant(it, ZoneId.of("UTC")))}" }
                ?: event.date?.let { "\n\n${DATE_FORMAT.format(it)}" }
                ?: ""
        val description = event.description?.let { "\n\n${it.tgEscape()}" } ?: ""
        return title + dateTime + description
    }

    private fun eventActionsMarkup(event: Event) = listOf(
        listOfNotNull(
            InlineKeyboardButton("Edit date").callbackData("/events/${event.id}/edit-date"),
            InlineKeyboardButton("Edit time").callbackData("/events/${event.id}/edit-time")
                .takeIf { event.date != null }
        ),
        listOf(
            InlineKeyboardButton("Edit name").callbackData("/events/${event.id}/edit-title"),
            InlineKeyboardButton("Edit description").callbackData("/events/${event.id}/edit-description")
        ),
        listOf(
            InlineKeyboardButton("Publish").callbackData("/events/${event.id}/publish"),
        )
    )

    private fun monthPicker(event: Event): List<List<InlineKeyboardButton>> {
        return YearMonth.now().let { current ->
            (0..<18).map { current.plusMonths(it.toLong()) }.chunked(3).map { yearMonthChunk ->
                yearMonthChunk.map { yearMonth ->
                    InlineKeyboardButton(MONTH_FORMAT.format(yearMonth))
                        .callbackData("/events/${event.id}/set-month/$yearMonth")
                }
            }
        } + cancelEditButton(event)
    }

    private fun dayPicker(event: Event, month: YearMonth): List<List<InlineKeyboardButton>> {
        val prefixDays = (1..<month.atDay(1).dayOfWeek.value).map { 0 }
        val isCurrentMonth = month == YearMonth.now()
        val pastDays = if (isCurrentMonth) (1..LocalDate.now().dayOfMonth).map { 0 } else listOf()
        val suffixDays = (month.atEndOfMonth().dayOfWeek.value..<7).map { 0 }
        val firstDay = if (isCurrentMonth) LocalDate.now().dayOfMonth + 1 else 1
        return (prefixDays + pastDays + (firstDay..month.lengthOfMonth()) + suffixDays)
            .chunked(7).map { dayChunk ->
                dayChunk.map { day ->
                    if (day > 0) {
                        val date = LocalDate.of(month.year, month.month, day)
                        InlineKeyboardButton(day.toString())
                            .callbackData("/events/${event.id}/set-date/$date")
                    } else {
                        InlineKeyboardButton(".").callbackData("noop")
                    }
                }
            } +
            listOf(listOf(InlineKeyboardButton("Back to month").callbackData("/events/${event.id}/edit-date"))) +
            cancelEditButton(event)
    }

    private fun partOfDayPicker(event: Event): List<List<InlineKeyboardButton>> {
        return listOf(
            listOf(InlineKeyboardButton("6am—10am").callbackData("/events/${event.id}/set-part-of-day/6")),
            listOf(InlineKeyboardButton("10am—2pm").callbackData("/events/${event.id}/set-part-of-day/10")),
            listOf(InlineKeyboardButton("2pm—6pm").callbackData("/events/${event.id}/set-part-of-day/14")),
            listOf(InlineKeyboardButton("6pm—10pm").callbackData("/events/${event.id}/set-part-of-day/18")),
            listOf(InlineKeyboardButton("10pm—2am").callbackData("/events/${event.id}/set-part-of-day/22")),
            listOf(InlineKeyboardButton("2am—6am").callbackData("/events/${event.id}/set-part-of-day/2")),
        ) + cancelEditButton(event)
    }

    private fun timePicker(event: Event, partOfDay: LocalTime): List<List<InlineKeyboardButton>> {
        return partOfDay.let { current ->
            (0..<16).map { current.plusMinutes(15 * it.toLong()) }.chunked(4).map { timeChunk ->
                timeChunk.map { time ->
                    InlineKeyboardButton(time.toString())
                        .callbackData("/events/${event.id}/set-time/${time.hour}:${time.minute}")
                }
            }
        } +
            listOf(listOf(InlineKeyboardButton("Back to part of day").callbackData("/events/${event.id}/edit-time"))) +
            cancelEditButton(event)
    }

    private fun cancelEditButton(event: Event): List<List<InlineKeyboardButton>> {
        return listOf(listOf(InlineKeyboardButton("Cancel").callbackData("/events/${event.id}/show")))
    }

    companion object {

        private val MONTH_FORMAT = DateTimeFormatter.ofPattern("MMM yyyy")
        private val DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("d MMM yyyy HH:mm")
        private val DATE_FORMAT = DateTimeFormatter.ofPattern("d MMM yyyy")

        private val EVENTS_NEW_REGEX = "/events/new\\?clubId=($UUID_REGEX)".toRegex()
        private val EVENTS_SHOW = "/events/(${UUID_REGEX})/show".toRegex()
        private val EVENTS_EDIT_DATE_REGEX = "/events/(${UUID_REGEX})/edit-date".toRegex()
        private val EVENTS_SET_MONTH_REGEX = "/events/(${UUID_REGEX})/set-month/(\\d{4}-\\d{2})".toRegex()
        private val EVENTS_SET_DATE_REGEX = "/events/(${UUID_REGEX})/set-date/(\\d{4}-\\d{2}-\\d{2})".toRegex()
        private val EVENTS_EDIT_TIME_REGEX = "/events/(${UUID_REGEX})/edit-time".toRegex()
        private val EVENTS_SET_PART_OF_DAY_REGEX = "/events/(${UUID_REGEX})/set-part-of-day/(\\d{1,2})".toRegex()
        private val EVENTS_SET_TIME_REGEX = "/events/(${UUID_REGEX})/set-time/(\\d{1,2}):(\\d{1,2})".toRegex()
        private val EVENTS_EDIT_TITLE_REGEX = "/events/(${UUID_REGEX})/edit-title".toRegex()
        private val EVENTS_EDIT_DESCRIPTION_REGEX = "/events/(${UUID_REGEX})/edit-description".toRegex()
        private val EVENTS_PUBLISH_REGEX = "/events/(${UUID_REGEX})/publish".toRegex()
        private val EVENTS_JOIN_REGEX = "/events/(${UUID_REGEX})/join".toRegex()
    }
}