package uk.matvey.drinki.bot.drink

import com.pengrad.telegrambot.TelegramBot
import uk.matvey.drinki.account.AccountRepo
import uk.matvey.drinki.drink.DrinkService
import uk.matvey.telek.TgEditMessageSupport.editMessage
import uk.matvey.telek.TgRequest
import uk.matvey.telek.TgSendMessageSupport.sendMessage

class EditDrinkName(
    private val accountRepo: AccountRepo,
    private val drinkService: DrinkService,
    private val bot: TelegramBot,
) {

    suspend operator fun invoke(rq: TgRequest) {
        val account = accountRepo.getByTgUserId(rq.userId())
        accountRepo.update(account.editingDrinkName())
        val drinkDetails = drinkService.getDrinkDetails(account.tgSession().drinkEdit().drinkId)
        bot.editMessage(
            rq.userId(),
            rq.messageId(),
            DrinkTg.drinkDetailsText(drinkDetails)
        )
        bot.sendMessage(rq.userId(), "New name:")
    }
}
