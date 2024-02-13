package uk.matvey.drinki.bot.drink

import uk.matvey.drinki.account.AccountRepo
import uk.matvey.drinki.drink.DrinkRepo
import uk.matvey.drinki.types.Amount
import uk.matvey.telek.TgRequest
import java.util.UUID

class AddDrinkIngredient(
    private val accountRepo: AccountRepo,
    private val drinkRepo: DrinkRepo,
    private val drinkTgService: DrinkTgService,
) {
    
    operator fun invoke(ingredientId: UUID, rq: TgRequest) {
        val account = accountRepo.getByTgUserId(rq.userId())
        val drink = drinkRepo.get(account.tgSession().drinkEdit().drinkId)
            .setIngredient(ingredientId, Amount.Some)
        drinkRepo.update(drink)
        accountRepo.update(account.updateTgEditDrinkIngredient(ingredientId))
        drinkTgService.editMessageDrinkDetailsIngredientSelected(drink.id, ingredientId, rq)
    }
}
