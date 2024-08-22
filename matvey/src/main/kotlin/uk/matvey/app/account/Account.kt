package uk.matvey.app.account

import kotlinx.serialization.Serializable
import java.util.UUID

data class Account(
    val id: UUID,
    val state: State,
    val name: String,
    val tags: Set<Tag>,
    val refs: Refs,
) {

    enum class State {
        ACTIVE,
        PENDING,
    }

    enum class Tag {
        ADMIN,
    }

    @Serializable
    data class Refs(
        val tg: Long?,
    )
}
