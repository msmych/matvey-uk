package uk.matvey.drinki.bot.drink

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup
import com.pengrad.telegrambot.model.request.ParseMode.MarkdownV2
import com.pengrad.telegrambot.request.EditMessageText
import com.pengrad.telegrambot.request.SendMessage
import uk.matvey.drinki.account.AccountRepo
import uk.matvey.drinki.drink.DrinkService
import uk.matvey.telek.TgRequest

class EditDrinkName(
    private val accountRepo: AccountRepo,
    private val drinkService: DrinkService,
    private val bot: TelegramBot,
) {
    
    operator fun invoke(rq: TgRequest) {
        val account = accountRepo.getByTgUserId(rq.userId())
        accountRepo.update(account.editingDrinkName())
        val drinkDetails = drinkService.getDrinkDetails(account.tgSession().drinkEdit().drinkId)
        bot.execute(
            EditMessageText(rq.userId(), rq.messageId(), DrinkTg.drinkDetailsText(drinkDetails))
                .parseMode(MarkdownV2)
                .replyMarkup(InlineKeyboardMarkup())
        )
        bot.execute(SendMessage(rq.userId(), "New name:"))
    }
}
