package uk.matvey.drinki.bot.amount

import com.pengrad.telegrambot.model.request.InlineKeyboardButton
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup
import uk.matvey.drinki.types.Amount
import uk.matvey.drinki.types.Amount.Oz
import uk.matvey.telek.Emoji.DELETE

object AmountTg {
    
    fun amountsKeyboard(): InlineKeyboardMarkup {
        return InlineKeyboardMarkup(
            amountsRow("some", "1/1oz", "2/1oz", "3/1oz"),
            amountsRow("1/4oz", "5/4oz", "9/4oz", "13/4oz"),
            amountsRow("1/3oz", "4/3oz", "7/3oz", "10/3oz"),
            amountsRow("1/2oz", "3/2oz", "5/2oz", "7/2oz"),
            amountsRow("2/3oz", "5/3oz", "8/3oz", "11/3oz"),
            amountsRow("3/4oz", "7/4oz", "11/4oz", "15/4oz"),
            arrayOf(InlineKeyboardButton("$DELETE Remove").callbackData("/drink_delete_ingredient"))
        )
    }
    
    fun Amount.label(): String {
        return when (this) {
            is Amount.Some -> "Some"
            is Oz -> this.label()
            else -> throw IllegalArgumentException()
        }
    }
    
    fun Oz.label(): String {
        val f = (n % d).let {
            when {
                it == 1 && d == 2 -> "¹⁄₂"
                it == 1 && d == 3 -> "¹⁄₃"
                it == 1 && d == 4 -> "¹⁄₄"
                it == 2 && d == 3 -> "⅔"
                it == 3 && d == 4 -> "¾"
                else -> "$it/$d"
            }
        }
        val w = (n / d)
        if (n % d == 0) {
            return "$w oz"
        }
        if (n % d == 0) {
            return "$w oz"
        }
        if (w == 0) {
            return "$f oz"
        }
        return "$w $f oz"
    }
    
    private fun amountsRow(vararg amounts: String): Array<InlineKeyboardButton> {
        return amounts.map(Amount.Companion::parse)
            .map { InlineKeyboardButton(it.label()).callbackData("/drink_ing_amt $it") }
            .toTypedArray()
    }
}
