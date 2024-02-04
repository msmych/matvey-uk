package uk.matvey.drinki.account

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import uk.matvey.drinki.account.Account.TgSession.DrinkEdit
import java.time.Instant
import java.util.*
import java.util.UUID.randomUUID

data class Account(
    val id: UUID,
    val tgSession: TgSession?,
    val createdAt: Instant,
    val updatedAt: Instant,
) {

    @Serializable
    data class TgSession(
        val userId: Long,
        val drinkEdit: DrinkEdit?,
    ) {

        @Serializable
        data class DrinkEdit(
            val drinkId: @Contextual UUID,
            val messageId: Int,
            val editingName: Boolean,
            val ingredientId: @Contextual UUID?,
            val editingRecipe: Boolean
        ) {
            fun ingredientId() = requireNotNull(ingredientId)
        }

        fun drinkEdit() = requireNotNull(drinkEdit)

    }

    fun tgSession() = requireNotNull(tgSession)

    fun editingDrink(drinkId: UUID, tgMessageId: Int): Account {
        return this.copy(
            tgSession = this.tgSession?.copy(
                drinkEdit = DrinkEdit(drinkId, tgMessageId, false, null, false)
            )
        )
    }

    fun editingDrinkName(): Account {
        return this.copy(
            tgSession = this.tgSession?.copy(
                drinkEdit = this.tgSession.drinkEdit?.copy(
                    editingName = true
                )
            )
        )
    }

    fun editingDrinkRecipe(): Account {
        return this.copy(
            tgSession = this.tgSession?.copy(
                drinkEdit = this.tgSession.drinkEdit?.copy(
                    editingRecipe = true
                )
            )
        )
    }

    fun updateTgEditDrinkIngredient(ingredientId: UUID): Account {
        return this.copy(
            tgSession = this.tgSession?.copy(
                drinkEdit = this.tgSession.drinkEdit?.copy(
                    ingredientId = ingredientId
                )
            )
        )
    }

    companion object {

        fun tgAccount(tgUserId: Long): Account {
            val now = Instant.now()
            return Account(
                randomUUID(),
                TgSession(tgUserId, null),
                now,
                now
            )
        }
    }
}
