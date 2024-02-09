package uk.matvey.drinki.bot.ingredient

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.request.EditMessageText
import uk.matvey.drinki.account.AccountRepo
import uk.matvey.drinki.ingredient.Ingredient
import uk.matvey.drinki.ingredient.IngredientRepo
import uk.matvey.telek.TgRequest

class SetIngredientType(
    private val accountRepo: AccountRepo,
    private val ingredientRepo: IngredientRepo,
    private val bot: TelegramBot,
) {

    operator fun invoke(type: Ingredient.Type, rq: TgRequest) {
        val account = accountRepo.getByTgUserId(rq.userId())
        val ingredient = ingredientRepo.get(account.tgSession().ingredientEdit().ingredientId)
            .setType(type)
        ingredientRepo.update(ingredient)
        bot.execute(
            EditMessageText(rq.userId(), account.tgSession().ingredientEdit().messageId, ingredient.name)
                .replyMarkup(IngredientTg.ingredientActionsKeyboard())
        )
    }
}
