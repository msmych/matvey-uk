package uk.matvey.begit.bot

import com.pengrad.telegrambot.model.CallbackQuery
import com.pengrad.telegrambot.model.Chat
import com.pengrad.telegrambot.model.Message
import com.pengrad.telegrambot.model.Update
import com.pengrad.telegrambot.model.User
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import uk.matvey.begit.event.EventUpdateProcessor
import uk.matvey.dukt.random.RandomSupport.randomLong
import uk.matvey.dukt.random.RandomSupport.randomStr
import uk.matvey.slon.Access
import uk.matvey.slon.Repo

class UpdateProcessorTest {

    private val repo = mockk<Repo>(relaxed = true)
    private val eventUpdateProcessor = mockk<EventUpdateProcessor>(relaxed = true)
    private val clubUpdateProcessor = mockk<ClubUpdateProcessor>(relaxed = true)

    private val updateProcessor = UpdateProcessor(repo, eventUpdateProcessor, clubUpdateProcessor)

    private val update = mockk<Update>()
    private val message = mockk<Message>()
    private val callbackQuery = mockk<CallbackQuery>()
    private val chat = mockk<Chat>()
    private val user = mockk<User>()
    private val chatId = randomLong()
    private val title = randomStr(12)
    private val userId = randomLong()

    @BeforeEach
    fun setup() {
        every { update.message() } returns null
        every { update.callbackQuery() } returns null
        every { message.chat() } returns chat
        every { chat.id() } returns chatId
        every { chat.title() } returns null
        every { message.from() } returns user
        every { user.id() } returns userId
        every { repo.access(any<(Access) -> Any?>())} returns null
    }

    @Test
    fun `should greet club`() {
        // given
        every { update.message() } returns message
        every { chat.title() } returns title
        every { message.text() } returns "/club"

        // when
        updateProcessor.process(update)

        // then

        verify { clubUpdateProcessor.greetClub(title, chatId) }
    }

    @Test
    fun `should handle clubs callback`() {
        // given
        every { update.callbackQuery() } returns callbackQuery
        every { callbackQuery.data() } returns "/clubs/..."

        // when
        updateProcessor.process(update)

        // then
        verify { clubUpdateProcessor.processCallback(callbackQuery) }
    }

    @Test
    fun `should handle events callback`() {
        // given
        every { update.callbackQuery() } returns callbackQuery
        every { callbackQuery.data() } returns "/events/..."

        // when
        updateProcessor.process(update)

        // then
        verify { eventUpdateProcessor.processCallback(callbackQuery) }
    }
}