package uk.matvey.app.wishlist

import java.net.URI
import java.time.Instant
import java.util.UUID

data class WishlistItem(
    val id: UUID,
    val name: String,
    val state: State,
    val priority: Priority,
    val description: String?,
    val url: URI?,
    val createdAt: Instant,
    val updatedAt: Instant,
) {
    
    enum class State {
        WANTED,
        RECEIVED,
        DELETED,
    }
    
    enum class Priority {
        HIGH,
        MEDIUM,
        LOW,
    }
}
