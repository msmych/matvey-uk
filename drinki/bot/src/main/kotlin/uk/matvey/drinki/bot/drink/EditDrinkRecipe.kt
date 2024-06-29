package uk.matvey.drinki.bot.drink

import com.pengrad.telegrambot.TelegramBot
import uk.matvey.drinki.account.AccountRepo
import uk.matvey.drinki.drink.DrinkService
import uk.matvey.telek.TgEditMessageSupport.editMessage
import uk.matvey.telek.TgRequest
import uk.matvey.telek.TgSendMessageSupport.sendMessage

class EditDrinkRecipe(
    private val accountRepo: AccountRepo,
    private val drinkService: DrinkService,
    private val bot: TelegramBot,
) {

    operator fun invoke(rq: TgRequest) {
        val account = accountRepo.getByTgUserId(rq.userId())
        val drinkDetails = drinkService.getDrinkDetails(account.tgSession().drinkEdit().drinkId)
        accountRepo.update(account.editingDrinkRecipe())
        bot.editMessage(
            rq.userId(),
            rq.messageId(),
            DrinkTg.drinkDetailsText(drinkDetails)
        )
        bot.sendMessage(rq.userId(), "New recipe:")
    }
}
