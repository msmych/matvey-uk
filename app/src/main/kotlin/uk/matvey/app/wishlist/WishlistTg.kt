package uk.matvey.app.wishlist

import com.pengrad.telegrambot.model.request.InlineKeyboardButton
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup
import uk.matvey.app.wishlist.WishlistItem.State
import uk.matvey.app.wishlist.WishlistItem.Tag
import uk.matvey.telek.TgSupport.tgEscape

object WishlistTg {
    
    fun wishlistMessageText(items: List<WishlistItem>): String {
        val text = "*Wishlist*\n\n" + items.joinToString("\n") { item ->
            "- ".tgEscape() + (if (item.state == State.LOCKED) {
                "🔒"
            } else {
                ""
            }) +
                (item.url?.let { "[${item.name.tgEscape()}](${it.toString().tgEscape()})" } ?: item.name) +
                (item.description?.let { description -> "\n${description.tgEscape()}" } ?: "")
        }
        return text
    }
    
    fun lockableWishlistItemsMarkup(items: List<WishlistItem>, userId: Long): InlineKeyboardMarkup {
        val markup = InlineKeyboardMarkup(
            *items.filter { it.tags.contains(Tag.LOCKABLE) }
                .filter { it.state == State.WANTED || (it.state == State.LOCKED && it.tg.lockedBy == userId) }
                .map { item ->
                    val lockText = if (item.state == State.LOCKED) "🔒" else "Lock "
                    arrayOf(
                        InlineKeyboardButton(lockText + item.name)
                            .callbackData("/wishlist_item_lock_toggle ${item.id}")
                    )
                }.toTypedArray()
        )
        return markup
    }
}
