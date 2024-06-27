package uk.matvey.begit.event

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import uk.matvey.begit.tg.TgSession.Data.TgChatMessageId
import java.net.URI
import java.time.Instant
import java.time.LocalDate
import java.util.UUID

data class Event(
    val id: UUID,
    val clubId: UUID,
    val organizedBy: UUID,
    val title: String,
    val description: String?,
    val type: Type,
    val date: LocalDate?,
    val dateTime: Instant?,
    val refs: Refs,
    val createdAt: Instant,
    val updatedAt: Instant?,
) {

    enum class Type {
        RUN,
        OTHER,
    }

    @Serializable
    data class Refs(
        val url: @Contextual URI? = null,
        val tgChatMessageId: TgChatMessageId? = null,
    ) {

        fun tgChatMessageId() = requireNotNull(this.tgChatMessageId)

        fun updateTgChatMessageId(chatId: Long, messageId: Int): Refs {
            return this.copy(
                tgChatMessageId = TgChatMessageId(chatId, messageId)
            )
        }
    }

    fun date() = requireNotNull(this.date)

    fun updateDate(date: LocalDate): Event {
        return this.copy(date = date)
    }

    fun updateTitle(title: String): Event {
        return this.copy(title = title)
    }

    fun updateDescription(description: String): Event {
        return this.copy(description = description)
    }

    fun updateTgChatMessageId(chatId: Long, messageId: Int): Event {
        return this.copy(refs = this.refs.updateTgChatMessageId(chatId, messageId))
    }

    fun updateTime(dateTime: Instant): Event {
        return this.copy(dateTime = dateTime)
    }
}
