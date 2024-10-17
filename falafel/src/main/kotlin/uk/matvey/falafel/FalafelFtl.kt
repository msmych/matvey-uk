package uk.matvey.falafel

import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.util.pipeline.PipelineContext
import uk.matvey.falafel.balance.AccountBalance
import uk.matvey.utka.ktor.ftl.FreeMarkerKit.respondFtl

object FalafelFtl {

    const val BASE_PATH = "/falafel"

    suspend fun PipelineContext<Unit, ApplicationCall>.respondIndex(
        account: AccountBalance,
        assets: String,
        loadPage: String?
    ) {
        call.respondFtl(
            "$BASE_PATH/index",
            "account" to account,
            "assets" to assets,
            "loadPage" to loadPage,
        )
    }
}
