package uk.matvey.falafel.tmdb

import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.util.pipeline.PipelineContext
import uk.matvey.utka.ktor.ftl.FreeMarkerKit.respondFtl
import java.util.UUID

object TmdbFtl {

    const val BASE_PATH = "/falafel/tmdb/"

    data class TitleId(val id: UUID)

    suspend fun PipelineContext<Unit, ApplicationCall>.respondMovieSaved(titleId: UUID) {
        call.respondFtl("$BASE_PATH/tmdb-movie-saved", "title" to TitleId(titleId))
    }
}
