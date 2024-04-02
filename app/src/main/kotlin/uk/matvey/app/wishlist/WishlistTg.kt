package uk.matvey.app.wishlist

import com.pengrad.telegrambot.model.request.InlineKeyboardButton
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup
import uk.matvey.app.wishlist.WishlistItem.State
import uk.matvey.app.wishlist.WishlistItem.Tag
import uk.matvey.telek.TgSupport.tgEscape

object WishlistTg {
    
    fun wishlistMessageText(items: List<WishlistItem>): String {
        val (lockable, nonLockable) = items.partition { it.tags.contains(Tag.LOCKABLE) }
        val text = "*Wishlist*\n" +
            ((
            "\nHere are some things I would love to receive as a gift".tgEscape() +
            " (you can lock some of them using buttons below):".tgEscape() +
            "\n" + lockable.joinToString("\n") { item ->
            "- ".tgEscape() + (if (item.state == State.LOCKED) {
                "🔒"
            } else {
                ""
            }) +
                (item.url?.let { "[*${item.name.tgEscape()}*](${it.toString().tgEscape()})" } ?: item.name) +
                (item.description?.let { description -> ". $description".tgEscape() } ?: "")
        }).takeIf { lockable.isNotEmpty() } ?: "") + "\n" + (("\n" + ("Also ".takeIf { lockable.isNotEmpty() } ?: "") + "I'm always happy to receive any of the following:\n".tgEscape() +
            nonLockable.joinToString("\n") { item ->
                "- ".tgEscape() +
                    (item.url?.let { "[*${item.name.tgEscape()}*](${it.toString().tgEscape()})" } ?: item.name) +
                    (item.description?.let { description -> ". $description".tgEscape() } ?: "")
            }).takeIf { nonLockable.isNotEmpty() } ?: "")
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
