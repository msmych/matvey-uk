package uk.matvey.begit.tg

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import uk.matvey.begit.tg.TgSession.Data.AwaitingAnswer
import java.time.Instant
import java.util.UUID

data class TgSession(
    val chatId: Long,
    val data: Data,
    val createdAt: Instant,
    val updatedAt: Instant?,
) {

    @Serializable
    data class Data(
        val eventId: @Contextual UUID? = null,
        val awaitingAnswer: AwaitingAnswer? = null,
        val chatMessageId: TgChatMessageId? = null,
    ) {

        enum class AwaitingAnswer {
            EVENT_TITLE,
            EVENT_DESCRIPTION,
        }

        @Serializable
        data class TgChatMessageId(
            val chatId: Long,
            val messageId: Int,
        )

        fun eventId() = requireNotNull(eventId)

        fun chatMessageId() = requireNotNull(chatMessageId)

        fun updateEventAwaitingAnswer(eventId: UUID, awaitingAnswer: AwaitingAnswer?): Data {
            return this.copy(
                awaitingAnswer = awaitingAnswer,
                eventId = eventId,
            )
        }

        fun updateChatMessageId(chatId: Long, messageId: Int): Data {
            return this.copy(
                chatMessageId = TgChatMessageId(chatId, messageId)
            )
        }
    }

    fun updateAwaitingAnswer(eventId: UUID, awaitingAnswer: AwaitingAnswer?): TgSession {
        return this.copy(
            data = this.data.updateEventAwaitingAnswer(eventId, awaitingAnswer)
        )
    }

    fun updateEditEventMessageId(chatId: Long, messageId: Int): TgSession {
        return this.copy(
            data = this.data.updateChatMessageId(chatId, messageId)
        )
    }
}
