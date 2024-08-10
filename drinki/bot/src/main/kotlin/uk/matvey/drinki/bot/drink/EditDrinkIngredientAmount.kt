package uk.matvey.drinki.bot.drink

import uk.matvey.drinki.account.AccountRepo
import uk.matvey.telek.TgRequest
import java.util.UUID

class EditDrinkIngredientAmount(
    private val accountRepo: AccountRepo,
    private val drinkTgService: DrinkTgService,
) {
    
    suspend operator fun invoke(ingredientId: UUID, rq: TgRequest) {
        val account = accountRepo.getByTgUserId(rq.userId())
            .updateTgEditDrinkIngredient(ingredientId)
        accountRepo.update(account)
        drinkTgService.editMessageDrinkDetailsIngredientSelected(
            account.tgSession().drinkEdit().drinkId,
            ingredientId,
            rq
        )
    }
}
