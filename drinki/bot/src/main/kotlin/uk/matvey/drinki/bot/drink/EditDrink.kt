package uk.matvey.drinki.bot.drink

import com.pengrad.telegrambot.TelegramBot
import uk.matvey.drinki.account.AccountRepo
import uk.matvey.drinki.drink.DrinkService
import uk.matvey.telek.TgEditMessageSupport.editMessage
import uk.matvey.telek.TgExecuteSupport.deleteMessage
import uk.matvey.telek.TgRequest
import java.util.UUID

class EditDrink(
    private val accountRepo: AccountRepo,
    private val drinkService: DrinkService,
    private val bot: TelegramBot,
) {

    suspend operator fun invoke(drinkId: UUID?, rq: TgRequest) {
        val account = accountRepo.getByTgUserId(rq.userId())
        val drinkDetails = drinkService.getDrinkDetails(drinkId ?: account.tgSession().drinkEdit().drinkId)
        if (drinkId != null) {
            account.tgSession().drinkEdit().let { (_, messageId) ->
                bot.deleteMessage(rq.userId(), messageId)
            }
        }
        bot.editMessage(
            rq.userId(),
            rq.messageId(),
            DrinkTg.drinkDetailsText(drinkDetails),
            DrinkTg.drinkActionsKeyboard(drinkDetails)
        )
        accountRepo.update(account.editingDrink(drinkDetails.id(), rq.messageId()))
    }
}
