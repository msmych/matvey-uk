package uk.matvey.dukt.json

import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import uk.matvey.dukt.json.JsonSupport.UriSerializer
import uk.matvey.dukt.json.JsonSupport.UuidSerializer
import uk.matvey.dukt.json.JsonSupport.YearMonthSerializer
import uk.matvey.dukt.json.JsonSupport.YearSerializer

object JsonSetup {
    
    val JSON = Json {
        serializersModule = SerializersModule {
            contextual(UuidSerializer)
            contextual(UriSerializer)
            contextual(YearSerializer)
            contextual(YearMonthSerializer)
        }
    }
}
