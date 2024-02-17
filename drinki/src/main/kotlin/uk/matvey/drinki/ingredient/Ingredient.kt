package uk.matvey.drinki.ingredient

import uk.matvey.drinki.types.Visibility
import java.time.Instant
import java.util.UUID
import java.util.UUID.randomUUID

data class Ingredient(
    val id: UUID,
    val accountId: UUID?,
    val type: Type?,
    val name: String,
    val visibility: Visibility,
    val createdAt: Instant,
    val updatedAt: Instant,
) {
    
    enum class Type {
        SPIRIT,
        BITTER,
        WINE,
        JUICE,
        SYRUP,
        TONIC,
        OTHER,
        ;
    }
    
    fun setName(name: String): Ingredient {
        return this.copy(
            name = name
        )
    }
    
    fun setType(type: Type): Ingredient {
        return this.copy(
            type = type
        )
    }
    
    fun toggleVisibility(): Ingredient {
        return this.copy(
            visibility = this.visibility.toggle()
        )
    }
    
    companion object {
        
        fun public(type: Type, name: String): Ingredient {
            val now = Instant.now()
            return Ingredient(
                randomUUID(),
                null,
                type,
                name,
                Visibility.PUBLIC,
                now,
                now
            )
        }
        
        fun private(accountId: UUID, name: String): Ingredient {
            val now = Instant.now()
            return Ingredient(
                randomUUID(),
                accountId,
                null,
                name,
                Visibility.PRIVATE,
                now,
                now
            )
        }
    }
}
