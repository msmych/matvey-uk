package uk.matvey.drinki.bot.drink

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.request.ParseMode.MarkdownV2
import com.pengrad.telegrambot.request.DeleteMessage
import com.pengrad.telegrambot.request.SendMessage
import uk.matvey.drinki.account.AccountRepo
import uk.matvey.drinki.drink.DrinkRepo
import uk.matvey.drinki.drink.DrinkService
import uk.matvey.telek.TgRequest

class SetDrinkName(
    private val accountRepo: AccountRepo,
    private val drinkRepo: DrinkRepo,
    private val drinkService: DrinkService,
    private val bot: TelegramBot,
) {
    
    operator fun invoke(name: String, rq: TgRequest) {
        val account = accountRepo.getByTgUserId(rq.userId())
        val drink = drinkRepo.get(account.tgSession().drinkEdit().drinkId)
            .setName(name)
        drinkRepo.update(drink)
        bot.execute(DeleteMessage(rq.userId(), account.tgSession().drinkEdit().messageId))
        val drinkDetails = drinkService.getDrinkDetails(drink.id)
        val result = bot.execute(
            SendMessage(
                rq.userId(),
                DrinkTg.drinkDetailsText(drinkDetails)
            )
                .parseMode(MarkdownV2)
                .replyMarkup(DrinkTg.drinkActionsKeyboard(drinkDetails))
        )
        accountRepo.update(account.editingDrink(drink.id, result.message().messageId()))
    }
}
