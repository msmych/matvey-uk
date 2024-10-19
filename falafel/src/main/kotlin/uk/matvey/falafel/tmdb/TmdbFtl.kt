package uk.matvey.falafel.tmdb

import java.util.UUID

object TmdbFtl {

    data class TmdbMovie(
        val id: String,
        val title: String,
        val releaseYear: String?,
        val titleId: UUID?,
    )
}
