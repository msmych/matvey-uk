package uk.matvey.drinki.drink

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.request.ParseMode.MarkdownV2
import com.pengrad.telegrambot.request.DeleteMessage
import com.pengrad.telegrambot.request.SendMessage
import uk.matvey.drinki.account.AccountRepo
import uk.matvey.drinki.account.AccountService
import uk.matvey.telek.TgRequest

class AddDrink(
    private val accountService: AccountService,
    private val accountRepo: AccountRepo,
    private val drinkRepo: DrinkRepo,
    private val bot: TelegramBot,
) {

    operator fun invoke(rq: TgRequest) {
        val account = accountService.ensureTgAccount(rq.userId())
        val drink = Drink.new(account.id)
        drinkRepo.add(drink)
        account.tgSession?.drinkEdit?.let { (_, messageId) ->
            bot.execute(DeleteMessage(rq.userId(), messageId))
        }
        val sendResult = bot.execute(
            SendMessage(rq.userId(), DrinkTg.drinkDetailsText(drink, listOf()))
                .parseMode(MarkdownV2)
                .replyMarkup(DrinkTg.drinkActionsKeyboard(drink))
        )
        accountRepo.update(account.editingDrink(drink.id, sendResult.message().messageId()))
    }
}