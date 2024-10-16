package uk.matvey.falafel.title

import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.util.pipeline.PipelineContext
import uk.matvey.utka.ktor.ftl.FreeMarkerKit.respondFtl

object TitleFtl {

    const val BASE_PATH = "/falafel/titles"

    suspend fun PipelineContext<Unit, ApplicationCall>.respondTitleDetails(title: Title) {
        call.respondFtl("$BASE_PATH/title-details", "title" to title)
    }
}
