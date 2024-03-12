package uk.matvey.dukt.json

import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import uk.matvey.dukt.json.JsonSupport.UuidSerializer

object JsonSetup {
    
    val JSON = Json {
        serializersModule = SerializersModule {
            contextual(UuidSerializer)
        }
    }
}
