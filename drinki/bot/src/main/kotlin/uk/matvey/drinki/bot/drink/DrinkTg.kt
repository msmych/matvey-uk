package uk.matvey.drinki.bot.drink

import com.pengrad.telegrambot.model.request.InlineKeyboardButton
import uk.matvey.drinki.bot.amount.AmountTg.label
import uk.matvey.drinki.drink.DrinkDetails
import uk.matvey.drinki.types.Visibility
import uk.matvey.telek.Emoji.COCKTAIL
import uk.matvey.telek.Emoji.DELETE
import uk.matvey.telek.Emoji.EDIT
import uk.matvey.telek.Emoji.NONE
import uk.matvey.telek.TgSupport.tgEscape

object DrinkTg {

    fun drinkTitle(name: String): String {
        return "$COCKTAIL *$name*"
    }

    fun drinkRecipe(recipe: String?): String {
        return recipe?.let { ">${it.tgEscape()}" } ?: "Recipe: $NONE"
    }

    fun drinkDetailsText(drink: DrinkDetails): String {
        val title = drinkTitle(drink.name())
        val ingredientsText = "\n\n" + if (drink.ingredients.isNotEmpty()) {
            drink.ingredients
                .map { (ingredient, amount) -> "- ${amount.label()} ${ingredient.name}".tgEscape() }
                .joinToString("\n")
        } else {
            "Ingredients: $NONE"
        }
        return title.tgEscape() + ingredientsText + "\n\n" + drinkRecipe(drink.recipe())
    }

    fun drinkActionsKeyboard(drink: DrinkDetails): List<List<InlineKeyboardButton>> {
        return listOf(
            listOf(
                InlineKeyboardButton("$EDIT Name").callbackData("/drink_edit_name"),
                InlineKeyboardButton("$EDIT Ingredients").callbackData("/drink_edit_ingredients"),
                InlineKeyboardButton("$EDIT Recipe").callbackData("/drink_edit_recipe"),
            ),
            listOf(
                InlineKeyboardButton(visibilityLabel(drink.visibility())).callbackData("/drink_toggle_visibility"),
                InlineKeyboardButton("$DELETE Delete").callbackData("/drink_delete")
            ),
        )
    }

    fun visibilityLabel(visibility: Visibility): String {
        return when (visibility) {
            Visibility.PRIVATE -> "🔒 Private"
            Visibility.PUBLIC -> "🥂 Public"
        }
    }
}
