package uk.matvey.drinki.bot.drink

import com.pengrad.telegrambot.model.request.InlineKeyboardButton
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup
import uk.matvey.drinki.bot.amount.AmountTg.label
import uk.matvey.drinki.drink.Drink
import uk.matvey.drinki.ingredient.Ingredient
import uk.matvey.drinki.types.Visibility
import uk.matvey.telek.Emoji.COCKTAIL
import uk.matvey.telek.Emoji.DELETE
import uk.matvey.telek.Emoji.EDIT
import uk.matvey.telek.Emoji.NONE
import uk.matvey.telek.TgSupport.escapeSpecial

object DrinkTg {


    fun drinkTitle(drink: Drink): String {
        return "$COCKTAIL *${drink.name}*"
    }

    fun drinkRecipe(drink: Drink): String {
        return drink.recipe?.let { ">$it" } ?: "Recipe: $NONE"
    }

    fun drinkDetailsText(drink: Drink, ingredients: List<Ingredient>): String {
        val title = drinkTitle(drink)
        val ingredientsText = "\n\n" + if (ingredients.isNotEmpty()) {
            ingredients.joinToString("\n") { "- ${drink.ingredientAmount(it.id)?.label()} ${it.name}" }
        } else {
            "Ingredients: $NONE"
        }
        return escapeSpecial(title + ingredientsText + "\n\n" + drinkRecipe(drink))
    }

    fun drinkActionsKeyboard(drink: Drink): InlineKeyboardMarkup {
        return InlineKeyboardMarkup(
            arrayOf(
                InlineKeyboardButton("$EDIT Name").callbackData("/drink_edit_name"),
                InlineKeyboardButton("$EDIT Ingredients").callbackData("/drink_edit_ingredients"),
                InlineKeyboardButton("$EDIT Recipe").callbackData("/drink_edit_recipe"),
            ),
            arrayOf(
                InlineKeyboardButton(visibilityLabel(drink.visibility)).callbackData("/drink_toggle_visibility"),
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
