package uk.matvey.drinki.bot.ingredient

import com.pengrad.telegrambot.TelegramBot
import uk.matvey.drinki.account.AccountRepo
import uk.matvey.drinki.account.AccountService
import uk.matvey.drinki.bot.ingredient.IngredientTg.ingredientActionsKeyboard
import uk.matvey.drinki.ingredient.Ingredient
import uk.matvey.drinki.ingredient.IngredientRepo
import uk.matvey.telek.TgEditMessageSupport.editMessage
import uk.matvey.telek.TgRequest

class AddIngredient(
    private val accountService: AccountService,
    private val accountRepo: AccountRepo,
    private val ingredientRepo: IngredientRepo,
    private val bot: TelegramBot,
) {
    
    suspend operator fun invoke(rq: TgRequest) {
        val account = accountService.ensureTgAccount(rq.userId())
        val ingredient = Ingredient.private(account.id, "New")
        ingredientRepo.add(ingredient)
        accountRepo.update(account.editingIngredient(ingredient.id, rq.messageId()))
        bot.editMessage(
            rq.userId(),
            rq.messageId(),
            ingredient.name,
            ingredientActionsKeyboard(ingredient)
        )
    }
}
