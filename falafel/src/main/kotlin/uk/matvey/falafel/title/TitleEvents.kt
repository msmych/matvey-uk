package uk.matvey.falafel.title

import kotlinx.coroutines.flow.MutableSharedFlow
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class TitleEvents {

    private val titleEvents = ConcurrentHashMap<UUID, MutableMap<String, MutableSharedFlow<Unit>>>()

    fun register(titleId: UUID, location: String) {
        titleEvents.putIfAbsent(titleId, ConcurrentHashMap())
        titleEvents[titleId]?.put(location, MutableSharedFlow())
    }

    suspend fun push(titleId: UUID) {
        titleEvents[titleId]?.forEach { (_, flow) ->
            flow.emit(Unit)
        }
    }

    suspend fun onEvent(titleId: UUID, location: String, block: suspend () -> Unit) {
        titleEvents[titleId]?.get(location)?.collect {
            try {
                block()
            } catch (e: Exception) {
                titleEvents[titleId]?.remove(location)
                if (titleEvents[titleId]?.isEmpty() == true) {
                    titleEvents.remove(titleId)
                }
            }
        }
    }
}
