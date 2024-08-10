package uk.matvey.begit.bot

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.CallbackQuery
import com.pengrad.telegrambot.model.Chat
import com.pengrad.telegrambot.model.Message
import com.pengrad.telegrambot.model.Update
import com.pengrad.telegrambot.model.User
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import uk.matvey.begit.athlete.AthleteUpdateProcessor
import uk.matvey.begit.event.EventUpdateProcessor
import uk.matvey.slon.repo.Repo
import kotlin.random.Random

class UpdateProcessorTest {

    private val repo = mockk<Repo>(relaxed = true)
    private val athleteUpdateProcessor = mockk<AthleteUpdateProcessor>(relaxed = true)
    private val eventUpdateProcessor = mockk<EventUpdateProcessor>(relaxed = true)
    private val clubUpdateProcessor = mockk<ClubUpdateProcessor>(relaxed = true)
    private val bot = mockk<TelegramBot>(relaxed = true)

    private val updateProcessor = UpdateProcessor(
        repo,
        athleteUpdateProcessor,
        eventUpdateProcessor,
        clubUpdateProcessor,
        bot
    )

    private val update = mockk<Update>()
    private val message = mockk<Message>()
    private val callbackQuery = mockk<CallbackQuery>()
    private val chat = mockk<Chat>()
    private val user = mockk<User>()
    private val chatId = Random.Default.nextLong()
    private val title = "Club Title"
    private val userId = Random.Default.nextLong()
    private val callbackQueryId = "callbackQueryId"

    @BeforeEach
    fun setUp() {
        every { update.message() } returns null
        every { update.callbackQuery() } returns null
        every { callbackQuery.id() } returns callbackQueryId
        every { callbackQuery.from() } returns user
        every { callbackQuery.maybeInaccessibleMessage() } returns message
        every { message.chat() } returns chat
        every { message.from() } returns user
        every { message.messageId() } returns Random.Default.nextInt()
        every { chat.id() } returns chatId
        every { chat.title() } returns null
        every { user.id() } returns userId
        every { user.username() } returns "username"
    }

    @Test
    fun `should greet club`() = runTest {
        // given
        every { update.message() } returns message
        every { chat.title() } returns title
        every { message.text() } returns "/club"

        // when
        updateProcessor.process(update)

        // then
        coVerify { clubUpdateProcessor.greetClub(title, chatId) }
    }

    @Test
    fun `should handle clubs callback`() = runTest {
        // given
        every { update.callbackQuery() } returns callbackQuery
        every { callbackQuery.data() } returns "/clubs/..."

        // when
        updateProcessor.process(update)

        // then
        coVerify { clubUpdateProcessor.processCallback(callbackQuery) }
    }

    @Test
    fun `should handle events callback`() = runTest {
        // given
        every { update.callbackQuery() } returns callbackQuery
        every { callbackQuery.data() } returns "/events/..."

        // when
        updateProcessor.process(update)

        // then
        coVerify { eventUpdateProcessor.processCallback(callbackQuery) }
    }
}