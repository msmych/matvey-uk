package uk.matvey.migraine.frobot

import kotlinx.serialization.Serializable
import uk.matvey.migraine.frobot.Frobot.State.BATTERY_LOW
import java.time.Instant
import java.util.UUID
import java.util.UUID.randomUUID

data class Frobot(
    val id: UUID,
    val state: State,
    val tg: Tg,
    val createdAt: Instant,
    val updatedAt: Instant,
) {
    
    enum class State {
        BATTERY_LOW,
        ACTIVE,
        OVERHEATED,
    }
    
    @Serializable
    data class Tg(
        val userId: Long,
        val rockGarden: RockGarden?,
    )
    
    @Serializable
    data class RockGarden(
        val messageId: Int?,
        val board: String?,
    ) {
        
        fun rockGardenBoard() = RockGardenBoard.fromString(requireNotNull(board))
    }
    
    fun rockGardenBoard() = requireNotNull(tg.rockGarden).rockGardenBoard()
    
    companion object {
        
        fun frobot(userId: Long): Frobot {
            val id = randomUUID()
            val now = Instant.now()
            return Frobot(id, BATTERY_LOW, Tg(userId,null), now, now)
        }
    }
}
