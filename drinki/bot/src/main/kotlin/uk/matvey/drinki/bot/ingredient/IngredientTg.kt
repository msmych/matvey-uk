package uk.matvey.drinki.bot.ingredient

import com.pengrad.telegrambot.model.request.InlineKeyboardButton
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup
import uk.matvey.drinki.bot.drink.DrinkTg
import uk.matvey.drinki.ingredient.Ingredient
import uk.matvey.telek.Emoji.DOT
import uk.matvey.telek.Emoji.EDIT
import java.util.UUID

object IngredientTg {
    
    fun ingredientText(ingredient: Ingredient): String {
        return """
            **${ingredient.name}**
             
            ${ingredient.type}
        """.trimIndent()
    }
    
    fun editDrinkIngredientsKeyboard(
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
                            InlineKeyboardButton("$DOT ${drinkIngredients[ingredient.id]?.name}")
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
    
    fun ingredientActionsKeyboard(ingredient: Ingredient): InlineKeyboardMarkup {
        return InlineKeyboardMarkup(
            arrayOf(
                InlineKeyboardButton("$EDIT Name").callbackData("/ingredient_edit_name"),
                InlineKeyboardButton("$EDIT Type").callbackData("/ingredient_edit_type"),
                InlineKeyboardButton(DrinkTg.visibilityLabel(ingredient.visibility))
                    .callbackData("/ingredient_toggle_visibility"),
            )
        )
    }
    
    fun ingredientTypeKeyboard(): InlineKeyboardMarkup {
        return InlineKeyboardMarkup(
            *Ingredient.Type.entries.map {
                arrayOf(InlineKeyboardButton(it.label()).callbackData("/ingredient_set_type ${it.name}"))
            }.toTypedArray()
        )
    }
    
    fun Ingredient.Type.label(): String {
        return this.name[0] + this.name.substring(1).lowercase()
    }
}
