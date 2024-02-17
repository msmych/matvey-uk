package uk.matvey.drinki.bot.drink

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.request.EditMessageText
import uk.matvey.drinki.account.AccountRepo
import uk.matvey.drinki.drink.DrinkRepo
import uk.matvey.telek.TgRequest

class DeleteDrink(
    private val accountRepo: AccountRepo,
    private val drinkRepo: DrinkRepo,
    private val bot: TelegramBot,
) {
    
    operator fun invoke(rq: TgRequest) {
        val account = accountRepo.getByTgUserId(rq.userId())
        drinkRepo.delete(account.tgSession().drinkEdit().drinkId)
        bot.execute(EditMessageText(rq.userId(), account.tgSession().drinkEdit().messageId, "Deleted"))
    }
}
