package uk.matvey.drinki.bot.ingredient

import com.pengrad.telegrambot.TelegramBot
import uk.matvey.drinki.account.AccountRepo
import uk.matvey.drinki.ingredient.IngredientRepo
import uk.matvey.telek.TgEditMessageSupport.editMessage
import uk.matvey.telek.TgRequest

class ToggleIngredientVisibility(
    private val accountRepo: AccountRepo,
    private val ingredientRepo: IngredientRepo,
    private val bot: TelegramBot,
) {

    operator fun invoke(rq: TgRequest) {
        val account = accountRepo.getByTgUserId(rq.userId())
        val ingredient = ingredientRepo.get(account.tgSession().ingredientEdit().ingredientId)
            .toggleVisibility()
        ingredientRepo.update(ingredient)
        bot.editMessage(
            rq.userId(),
            account.tgSession().ingredientEdit().messageId,
            IngredientTg.ingredientText(ingredient),
            IngredientTg.ingredientActionsKeyboard(ingredient)
        )
    }
}
