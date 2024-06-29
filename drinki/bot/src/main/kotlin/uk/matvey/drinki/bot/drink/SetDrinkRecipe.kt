package uk.matvey.drinki.bot.drink

import com.pengrad.telegrambot.TelegramBot
import uk.matvey.drinki.account.AccountRepo
import uk.matvey.drinki.drink.DrinkRepo
import uk.matvey.drinki.drink.DrinkService
import uk.matvey.telek.TgExecuteSupport.deleteMessage
import uk.matvey.telek.TgRequest
import uk.matvey.telek.TgSendMessageSupport.sendMessage

class SetDrinkRecipe(
    private val accountRepo: AccountRepo,
    private val drinkRepo: DrinkRepo,
    private val drinkService: DrinkService,
    private val bot: TelegramBot,
) {

    operator fun invoke(recipe: String, rq: TgRequest) {
        val account = accountRepo.getByTgUserId(rq.userId())
        val drink = drinkRepo.get(account.tgSession().drinkEdit().drinkId)
            .setRecipe(recipe)
        drinkRepo.update(drink)
        bot.deleteMessage(rq.userId(), account.tgSession().drinkEdit().messageId)
        val drinkDetails = drinkService.getDrinkDetails(drink.id)
        val result = bot.sendMessage(
            rq.userId(),
            DrinkTg.drinkDetailsText(drinkDetails),
            DrinkTg.drinkActionsKeyboard(drinkDetails)
        )
        accountRepo.update(account.editingDrink(drink.id, result.message().messageId()))
    }
}
