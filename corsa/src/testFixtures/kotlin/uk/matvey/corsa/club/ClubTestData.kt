package uk.matvey.corsa.club

import uk.matvey.kit.random.RandomKit.randomAlphabetic
import java.util.UUID
import java.util.UUID.randomUUID

object ClubTestData {

    fun aClub(
        id: UUID = randomUUID(),
        name: String = randomAlphabetic(),
    ) = Club(
        id = id,
        name = name,
    )
}