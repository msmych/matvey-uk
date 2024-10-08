package uk.matvey.drinki.bot.drink

import com.pengrad.telegrambot.TelegramBot
import uk.matvey.drinki.account.AccountRepo
import uk.matvey.drinki.drink.DrinkRepo
import uk.matvey.drinki.drink.DrinkService
import uk.matvey.telek.TgEditMessageSupport.editMessage
import uk.matvey.telek.TgRequest

class ToggleDrinkVisibility(
    private val accountRepo: AccountRepo,
    private val drinkRepo: DrinkRepo,
    private val drinkService: DrinkService,
    private val bot: TelegramBot,
) {
    
    suspend operator fun invoke(rq: TgRequest) {
        val account = accountRepo.getByTgUserId(rq.userId())
        val drink = drinkRepo.get(account.tgSession().drinkEdit().drinkId)
            .toggleVisibility()
        drinkRepo.update(drink)
        val drinkDetails = drinkService.getDrinkDetails(drink.id)
        bot.editMessage(
            rq.userId(),
            rq.messageId(),
            DrinkTg.drinkDetailsText(drinkDetails),
            DrinkTg.drinkActionsKeyboard(drinkDetails)
        )
    }
}
