package uk.matvey.drinki.bot.drink

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.request.ParseMode.MarkdownV2
import com.pengrad.telegrambot.request.EditMessageText
import uk.matvey.drinki.bot.amount.AmountTg
import uk.matvey.drinki.drink.DrinkDetails
import uk.matvey.drinki.drink.DrinkService
import uk.matvey.telek.TgEditMessageSupport.editMessage
import uk.matvey.telek.TgRequest
import java.util.UUID

class DrinkTgService(
    private val drinkService: DrinkService,
    private val bot: TelegramBot,
) {
    
    fun editMessageDrinkDetailsIngredientSelected(
        drinkId: UUID,
        currentIngredientId: UUID,
        rq: TgRequest
    ) {
        val drinkDetails = drinkService.getDrinkDetails(drinkId)
        val (currentIngredient, otherDrinkIngredients) = drinkDetails.ingredients.keys
            .partition { it.id == currentIngredientId }
            .let { (c, o) -> c.single() to o }
        val messageText = DrinkTg.drinkDetailsText(DrinkDetails.from(drinkDetails.drink, otherDrinkIngredients)) +
            "\n\n${currentIngredient.name}:"
        bot.editMessage(
                rq.userId(),
                rq.messageId(),
                messageText,
                AmountTg.amountsKeyboard()
        )
    }
}
