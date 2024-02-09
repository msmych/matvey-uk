package uk.matvey.drinki.bot.ingredient

import com.pengrad.telegrambot.model.request.InlineKeyboardButton
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup
import uk.matvey.drinki.bot.amount.AmountTg.label
import uk.matvey.drinki.drink.Drink
import uk.matvey.drinki.ingredient.Ingredient
import uk.matvey.telek.Emoji.DOT
import uk.matvey.telek.Emoji.EDIT
import java.util.UUID

object IngredientTg {

    fun editDrinkIngredientsKeyboard(
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
                            InlineKeyboardButton("$DOT ${amount?.label()} ${drinkIngredients[ingredient.id]?.name}")
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

    fun ingredientActionsKeyboard(): InlineKeyboardMarkup {
        return InlineKeyboardMarkup(
            arrayOf(
                InlineKeyboardButton("$EDIT Name").callbackData("/ingredient_edit_name"),
                InlineKeyboardButton("$EDIT Type").callbackData("/ingredient_edit_type"),
            )
        )
    }

    fun ingredientTypeKeyboard(): InlineKeyboardMarkup {
        return InlineKeyboardMarkup(
            Ingredient.Type.entries.map {
                InlineKeyboardButton(it.name).callbackData("/ingredient_set_type ${it.name}")
            }.toTypedArray()
        )
    }
}
