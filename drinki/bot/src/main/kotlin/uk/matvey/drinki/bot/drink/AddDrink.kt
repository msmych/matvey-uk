package uk.matvey.drinki.bot.drink

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.request.DeleteMessage
import uk.matvey.drinki.account.AccountRepo
import uk.matvey.drinki.account.AccountService
import uk.matvey.drinki.drink.Drink
import uk.matvey.drinki.drink.DrinkDetails
import uk.matvey.drinki.drink.DrinkRepo
import uk.matvey.telek.TgRequest
import uk.matvey.telek.TgSendMessageSupport.sendMessage

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
        val drinkDetails = DrinkDetails.from(drink, listOf())
        val sendResult = bot.sendMessage(
            rq.userId(),
            DrinkTg.drinkDetailsText(drinkDetails),
            DrinkTg.drinkActionsKeyboard(drinkDetails)
        )
        accountRepo.update(account.editingDrink(drink.id, sendResult.message().messageId()))
    }
}
