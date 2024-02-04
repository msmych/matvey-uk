package uk.matvey.drinki.drink

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup
import com.pengrad.telegrambot.model.request.ParseMode.MarkdownV2
import com.pengrad.telegrambot.request.EditMessageText
import com.pengrad.telegrambot.request.SendMessage
import uk.matvey.drinki.account.AccountRepo
import uk.matvey.telek.TgRequest

class EditDrinkName(
    private val accountRepo: AccountRepo,
    private val drinkRepo: DrinkRepo,
    private val bot: TelegramBot,
) {

    operator fun invoke(rq: TgRequest) {
        val account = accountRepo.getByTgUserId(rq.userId())
        val drink = drinkRepo.get(account.tgSession().drinkEdit().drinkId)
        accountRepo.update(account.editingDrinkName())
        bot.execute(
            EditMessageText(rq.userId(), rq.messageId(), DrinkTg.drinkTitle(drink))
                .parseMode(MarkdownV2)
                .replyMarkup(InlineKeyboardMarkup())
        )
        bot.execute(SendMessage(rq.userId(), "New name:"))
    }
}