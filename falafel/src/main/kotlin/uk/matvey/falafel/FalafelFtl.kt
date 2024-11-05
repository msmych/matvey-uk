package uk.matvey.falafel

import com.typesafe.config.Config
import io.ktor.server.application.ApplicationCall
import uk.matvey.utka.ktor.ftl.FreeMarkerKit.respondFtl

class FalafelFtl(
    serverConfig: Config,
    private val auth: FalafelAuth,
) {

    private val assets = serverConfig.getString("assets")

    suspend fun respondIndex(
        call: ApplicationCall,
        loadPage: String,
    ) {
        val account = auth.getAccountBalanceOrNull(call)
        call.respondFtl(
            "/falafel/index",
            "account" to account,
            "assets" to assets,
            "loadPage" to loadPage,
        )
    }
}
