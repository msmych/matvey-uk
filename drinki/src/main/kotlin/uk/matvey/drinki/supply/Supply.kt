package uk.matvey.drinki.supply

import uk.matvey.drinki.types.Amount
import java.util.*

data class Supply(
    val id: UUID,
    val accountId: UUID,
    val ingredientId: UUID,
    val amount: Amount,
)
