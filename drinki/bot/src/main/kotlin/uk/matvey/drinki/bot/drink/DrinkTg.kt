package uk.matvey.drinki.bot.drink

import com.pengrad.telegrambot.model.request.InlineKeyboardButton
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup
import uk.matvey.drinki.drink.Drink
import uk.matvey.drinki.ingredient.Ingredient
import uk.matvey.drinki.bot.amount.AmountTg.label
import uk.matvey.drinki.types.Visibility
import uk.matvey.telek.TgSupport.escapeSpecial

object DrinkTg {

    const val COCKTAIL_EMOJI = "\uD83C\uDF79"
    const val NONE_EMOJI = "\uD83E\uDD37\u200D♀\uFE0F"
    const val EDIT_EMOJI = "📝"
    const val DELETE_EMOJI = "❌"
    const val DOT_EMOJI = "▫️"

    fun drinkTitle(drink: Drink): String {
        return "$COCKTAIL_EMOJI *${drink.name}*"
    }

    fun drinkRecipe(drink: Drink): String {
        return drink.recipe?.let { ">$it" } ?: "Recipe: $NONE_EMOJI"
    }

    fun drinkDetailsText(drink: Drink, ingredients: List<Ingredient>): String {
        val title = drinkTitle(drink)
        val ingredientsText = "\n\n" + if (ingredients.isNotEmpty()) {
            ingredients.joinToString("\n") { "- ${drink.ingredientAmount(it.id)?.label()} ${it.name}" }
        } else {
            "Ingredients: $NONE_EMOJI"
        }
        return escapeSpecial(title + ingredientsText + "\n\n" + drinkRecipe(drink))
    }

    fun drinkActionsKeyboard(drink: Drink): InlineKeyboardMarkup {
        return InlineKeyboardMarkup(
            arrayOf(
                InlineKeyboardButton("$EDIT_EMOJI Name").callbackData("/drink_edit_name"),
                InlineKeyboardButton("$EDIT_EMOJI Ingredients").callbackData("/drink_edit_ingredients"),
                InlineKeyboardButton("$EDIT_EMOJI Recipe").callbackData("/drink_edit_recipe"),
            ),
            arrayOf(
                InlineKeyboardButton(visibilityLabel(drink.visibility)).callbackData("/drink_toggle_visibility"),
                InlineKeyboardButton("$DELETE_EMOJI Delete").callbackData("/drink_delete")
            ),
        )
    }

    private fun visibilityLabel(visibility: Visibility): String {
        return when (visibility) {
            Visibility.PRIVATE -> "🔒 Private"
            Visibility.PUBLIC -> "🥂 Public"
        }
    }
}
