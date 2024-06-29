package uk.matvey.begit.club

import kotlinx.serialization.Serializable
import uk.matvey.begit.tg.TgSession.Data.TgChatMessageId
import java.time.Instant
import java.util.UUID

data class Club(
    val id: UUID,
    val name: String,
    val description: String?,
    val refs: Refs,
    val createdAt: Instant,
    val updatedAt: Instant,
) {
    @Serializable
    data class Refs(
        val tgChatId: Long,
        val tgChatMessageId: TgChatMessageId? = null,
    ) {
        fun updateTgChatMessageId(messageId: Int): Refs {
            return copy(tgChatMessageId = TgChatMessageId(this.tgChatId, messageId))
        }
    }

    fun updateTgChatMessageId(messageId: Int): Club {
        return copy(refs = this.refs.updateTgChatMessageId(messageId))
    }
}