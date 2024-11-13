package uk.matvey.falafel.club

import java.util.UUID

class ClubTitle(
    val clubId: UUID,
    val titleId: UUID,
    val watched: Boolean,
    val saved: Boolean,
) {
}
