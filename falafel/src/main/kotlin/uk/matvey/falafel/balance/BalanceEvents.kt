package uk.matvey.falafel.balance

import kotlinx.coroutines.flow.MutableSharedFlow
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class BalanceEvents {

    private val balanceEvents = ConcurrentHashMap<UUID, MutableMap<String, MutableSharedFlow<Int>>>()

    fun register(accountId: UUID, location: String) {
        balanceEvents.putIfAbsent(accountId, ConcurrentHashMap())
        balanceEvents[accountId]?.put(location, MutableSharedFlow())
    }

    suspend fun push(accountId: UUID, value: Int) {
        balanceEvents[accountId]?.forEach { (_, flow) ->
            flow.emit(value)
        }
    }

    suspend fun onEvent(accountId: UUID, location: String, block: suspend (Int) -> Unit) {
        balanceEvents[accountId]?.get(location)?.collect {
            try {
                block(it)
            } catch (e: Exception) {
                balanceEvents[accountId]?.remove(location)
                if (balanceEvents[accountId]?.isEmpty() == true) {
                    balanceEvents.remove(accountId)
                }
            }
        }
    }
}
