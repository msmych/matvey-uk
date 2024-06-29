package uk.matvey.drinki.bot.drink

import com.pengrad.telegrambot.TelegramBot
import uk.matvey.drinki.account.AccountRepo
import uk.matvey.drinki.bot.ingredient.IngredientTg
import uk.matvey.drinki.drink.DrinkService
import uk.matvey.drinki.ingredient.IngredientRepo
import uk.matvey.telek.TgEditMessageSupport.editMessage
import uk.matvey.telek.TgRequest

class EditDrinkIngredients(
    private val accountRepo: AccountRepo,
    private val ingredientRepo: IngredientRepo,
    private val drinkService: DrinkService,
    private val bot: TelegramBot,
) {
    
    operator fun invoke(rq: TgRequest) {
        val account = accountRepo.getByTgUserId(rq.userId())
        val drink = drinkService.getDrinkDetails(account.tgSession().drinkEdit().drinkId)
        val publicIngredients = ingredientRepo.publicIngredients()
        bot.editMessage(
                rq.userId(),
                rq.messageId(),
                DrinkTg.drinkDetailsText(drink),
                    IngredientTg.editDrinkIngredientsKeyboard(
                        drink.ingredients.keys.associateBy { it.id },
                        publicIngredients
                    )
        )
    }
}
