package uk.matvey.drinki.drink

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup
import com.pengrad.telegrambot.model.request.ParseMode.MarkdownV2
import com.pengrad.telegrambot.request.EditMessageText
import com.pengrad.telegrambot.request.SendMessage
import uk.matvey.drinki.account.AccountRepo
import uk.matvey.telek.TgRequest

class EditDrinkRecipe(
    private val accountRepo: AccountRepo,
    private val drinkRepo: DrinkRepo,
    private val bot: TelegramBot,
) {

    operator fun invoke(rq: TgRequest) {
        val account = accountRepo.getByTgUserId(rq.userId())
        val drink = drinkRepo.get(account.tgSession().drinkEdit().drinkId)
        accountRepo.update(account.editingDrinkRecipe())
        bot.execute(
            EditMessageText(
                rq.userId(),
                rq.messageId(),
                DrinkTg.drinkTitle(drink) + "\n\n" + DrinkTg.drinkRecipe(drink)
            )
                .replyMarkup(InlineKeyboardMarkup())
                .parseMode(MarkdownV2)
        )
        bot.execute(SendMessage(rq.userId(), "New recipe:"))
    }
}