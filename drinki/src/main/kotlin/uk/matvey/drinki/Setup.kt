package uk.matvey.drinki

import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import uk.matvey.dukt.JsonSupport.UuidSerializer

object Setup {

    val JSON = Json {
        serializersModule = SerializersModule {
            contextual(UuidSerializer)
        }
    }
}