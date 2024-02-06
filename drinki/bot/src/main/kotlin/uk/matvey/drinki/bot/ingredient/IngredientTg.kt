package uk.matvey.drinki.bot.ingredient

import com.pengrad.telegrambot.model.request.InlineKeyboardButton
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup
import uk.matvey.drinki.drink.Drink
import uk.matvey.drinki.bot.drink.DrinkTg.DOT_EMOJI
import uk.matvey.drinki.ingredient.Ingredient
import uk.matvey.drinki.bot.amount.AmountTg.label
import java.util.*

object IngredientTg {

    fun editIngredientsKeyboard(
        drink: Drink,
        drinkIngredients: Map<UUID, Ingredient>,
        publicIngredients: List<Ingredient>
    ): InlineKeyboardMarkup {
        return InlineKeyboardMarkup(
            *publicIngredients
                .sortedBy { it.type }
                .chunked(3)
                .map { row ->
                    row.map { ingredient ->
                        if (drinkIngredients.containsKey(ingredient.id)) {
                            val amount = drink.ingredientAmount(ingredient.id)
                            InlineKeyboardButton("$DOT_EMOJI ${amount?.label()} ${drinkIngredients[ingredient.id]?.name}")
                                .callbackData("/drink_edit_ingredient ${ingredient.id}")
                        } else {
                            InlineKeyboardButton(ingredient.name)
                                .callbackData("/drink_add_ing ${ingredient.id}")
                        }
                    }.toTypedArray()
                }.toTypedArray(),
            arrayOf(InlineKeyboardButton("Done").callbackData("/drink_edit"))
        )
    }
}
