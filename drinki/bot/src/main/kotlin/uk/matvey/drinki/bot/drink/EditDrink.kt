package uk.matvey.drinki.bot.drink

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.request.ParseMode.MarkdownV2
import com.pengrad.telegrambot.request.DeleteMessage
import com.pengrad.telegrambot.request.EditMessageText
import uk.matvey.drinki.account.AccountRepo
import uk.matvey.drinki.drink.DrinkService
import uk.matvey.telek.TgRequest
import java.util.UUID

class EditDrink(
    private val accountRepo: AccountRepo,
    private val drinkService: DrinkService,
    private val bot: TelegramBot,
) {
    
    operator fun invoke(drinkId: UUID?, rq: TgRequest) {
        val account = accountRepo.getByTgUserId(rq.userId())
        val drinkDetails = drinkService.getDrinkDetails(drinkId ?: account.tgSession().drinkEdit().drinkId)
        if (drinkId != null) {
            account.tgSession().drinkEdit().let { (_, messageId) ->
                bot.execute(DeleteMessage(rq.userId(), messageId))
            }
        }
        bot.execute(
            EditMessageText(rq.userId(), rq.messageId(), DrinkTg.drinkDetailsText(drinkDetails))
                .parseMode(MarkdownV2)
                .replyMarkup(DrinkTg.drinkActionsKeyboard(drinkDetails))
        )
        accountRepo.update(account.editingDrink(drinkDetails.id, rq.messageId()))
    }
}
