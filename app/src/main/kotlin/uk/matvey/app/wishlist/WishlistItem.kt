package uk.matvey.app.wishlist

import kotlinx.serialization.Serializable
import java.net.URI
import java.time.Instant
import java.util.UUID

data class WishlistItem(
    val id: UUID,
    val name: String,
    val state: State,
    val tags: Set<Tag>,
    val description: String?,
    val url: URI?,
    val tg: Tg,
    val createdAt: Instant,
    val updatedAt: Instant,
) {
    
    @Serializable
    data class Tg(
        val lockedBy: Long? = null,
    )
    
    enum class State {
        WANTED,
        LOCKED,
        RECEIVED,
        DELETED,
    }
    
    enum class Tag {
        LOCKABLE,
    }
    
    fun toggleLock(userId: Long) = copy(
        state = when (state) {
            State.WANTED -> State.LOCKED
            State.LOCKED -> State.WANTED
            else -> state
        },
        tg = tg.copy(lockedBy = userId.takeUnless { state == State.LOCKED })
    )
}
