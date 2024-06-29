package uk.matvey.drinki.bot.drink

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.request.InlineKeyboardButton
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup
import com.pengrad.telegrambot.request.DeleteMessage
import com.pengrad.telegrambot.request.SendMessage
import uk.matvey.drinki.account.AccountRepo
import uk.matvey.drinki.drink.DrinkRepo
import uk.matvey.drinki.drink.DrinkService
import uk.matvey.telek.TgRequest
import uk.matvey.telek.TgSendMessageSupport.sendMessage

class SearchDrinks(
    private val accountRepo: AccountRepo,
    private val drinkRepo: DrinkRepo,
    private val drinkService: DrinkService,
    private val bot: TelegramBot,
) {

    operator fun invoke(query: String, rq: TgRequest) {
        val account = accountRepo.getByTgUserId(rq.userId())
        val drinks = drinkRepo.search(account.id, query)
        when (drinks.size) {
            0 -> bot.execute(SendMessage(rq.userId(), "No results").replyToMessageId(rq.messageId()))
            1 -> {
                account.tgSession?.drinkEdit?.let { (_, messageId) ->
                    bot.execute(DeleteMessage(rq.userId(), messageId))
                }
                val drink = drinkService.getDrinkDetails(drinks.single().id)
                bot.sendMessage(
                    rq.userId(),
                    DrinkTg.drinkDetailsText(drink),
                    DrinkTg.drinkActionsKeyboard(drink)
                )
                accountRepo.update(account.editingDrink(drink.id(), rq.messageId()))
            }

            else -> {
                bot.execute(
                    SendMessage(rq.userId(), "Results:")
                        .replyToMessageId(rq.messageId())
                        .replyMarkup(InlineKeyboardMarkup(
                            drinks
                                .map { InlineKeyboardButton(it.name).callbackData("/drink_edit ${it.id}") }
                                .toTypedArray()
                        ))
                )
            }
        }
    }
}
