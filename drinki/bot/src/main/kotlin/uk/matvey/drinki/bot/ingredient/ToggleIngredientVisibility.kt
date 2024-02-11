package uk.matvey.drinki.bot.ingredient

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.request.EditMessageText
import uk.matvey.drinki.account.AccountRepo
import uk.matvey.drinki.ingredient.IngredientRepo
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
        bot.execute(
            EditMessageText(
                rq.userId(),
                account.tgSession().ingredientEdit().messageId,
                IngredientTg.ingredientText(ingredient)
            )
                .replyMarkup(IngredientTg.ingredientActionsKeyboard(ingredient))
        )
    }
}