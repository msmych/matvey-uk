package uk.matvey.drinki.bot.ingredient

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.request.EditMessageText
import uk.matvey.drinki.account.AccountRepo
import uk.matvey.drinki.account.AccountService
import uk.matvey.drinki.bot.ingredient.IngredientTg.ingredientActionsKeyboard
import uk.matvey.drinki.ingredient.Ingredient
import uk.matvey.drinki.ingredient.IngredientRepo
import uk.matvey.telek.TgRequest

class AddIngredient(
    private val accountService: AccountService,
    private val accountRepo: AccountRepo,
    private val ingredientRepo: IngredientRepo,
    private val bot: TelegramBot,
) {

    operator fun invoke(rq: TgRequest) {
        val account = accountService.ensureTgAccount(rq.userId())
        val ingredient = Ingredient.private(account.id, "New")
        ingredientRepo.add(ingredient)
        accountRepo.update(account.editingIngredient(ingredient.id, rq.messageId()))
        bot.execute(
            EditMessageText(rq.userId(), rq.messageId(), ingredient.name)
                .replyMarkup(ingredientActionsKeyboard(ingredient))
        )
    }
}
