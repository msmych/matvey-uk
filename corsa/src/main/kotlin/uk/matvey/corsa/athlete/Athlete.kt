package uk.matvey.corsa.athlete

import kotlinx.serialization.Serializable
import java.util.UUID

data class Athlete(
    val id: UUID,
    val name: String,
    val refs: Refs,
) {

    @Serializable
    data class Refs(
        val tg: Long?,
    )
}
