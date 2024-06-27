package uk.matvey.dukt.json

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.net.URI
import java.time.Year
import java.time.YearMonth
import java.util.UUID

object JsonSupport {

    object UuidSerializer : KSerializer<UUID> {
        override val descriptor = PrimitiveSerialDescriptor("UUID", PrimitiveKind.STRING)

        override fun deserialize(decoder: Decoder): UUID {
            return UUID.fromString(decoder.decodeString())
        }

        override fun serialize(encoder: Encoder, value: UUID) {
            return encoder.encodeString(value.toString())
        }
    }

    object UriSerializer : KSerializer<URI> {
        override val descriptor = PrimitiveSerialDescriptor("URI", PrimitiveKind.STRING)

        override fun deserialize(decoder: Decoder): URI {
            return URI(decoder.decodeString())
        }

        override fun serialize(encoder: Encoder, value: URI) {
            return encoder.encodeString(value.toString())
        }
    }

    object YearSerializer : KSerializer<Year> {
        override val descriptor = PrimitiveSerialDescriptor("Year", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder): Year {
            return Year.of(decoder.decodeInt())
        }

        override fun serialize(encoder: Encoder, value: Year) {
            return encoder.encodeInt(value.value)
        }
    }

    object YearMonthSerializer : KSerializer<YearMonth> {
        override val descriptor = PrimitiveSerialDescriptor("YearMonth", PrimitiveKind.STRING)

        override fun deserialize(decoder: Decoder): YearMonth {
            return YearMonth.parse(decoder.decodeString())
        }

        override fun serialize(encoder: Encoder, value: YearMonth) {
            return encoder.encodeString(value.toString())
        }
    }
}
