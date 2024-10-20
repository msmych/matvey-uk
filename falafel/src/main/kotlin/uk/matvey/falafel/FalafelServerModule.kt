package uk.matvey.falafel

import com.typesafe.config.Config
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCallPipeline
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.auth.authenticate
import io.ktor.server.request.header
import io.ktor.server.request.uri
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.ktor.server.sse.SSE
import kotlinx.coroutines.flow.MutableSharedFlow
import uk.matvey.falafel.club.ClubResource
import uk.matvey.falafel.club.ClubService
import uk.matvey.falafel.tag.TagResource
import uk.matvey.falafel.tag.TagService
import uk.matvey.falafel.title.TitleResource
import uk.matvey.falafel.tmdb.TmdbResource
import uk.matvey.slon.repo.Repo
import uk.matvey.tmdb.TmdbClient
import uk.matvey.utka.ktor.ftl.FreeMarkerKit.respondFtl
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

fun Application.falafelServerModule(
    serverConfig: Config,
    falafelAuth: FalafelAuth,
    repo: Repo,
    tmdbClient: TmdbClient,
) {
    val clubService = ClubService(repo)
    val tagService = TagService(repo)
    val titlesEvents = ConcurrentHashMap<UUID, MutableSharedFlow<String>>()
    val resources = listOf(
        ClubResource(repo, clubService),
        TitleResource(falafelAuth, repo, tagService, titlesEvents),
        TagResource(falafelAuth, repo, tagService, titlesEvents),
        TmdbResource(falafelAuth, tmdbClient, repo),
    )
    val assets = serverConfig.getString("assets")
    install(SSE)
    routing {
        route("/falafel") {
            authenticate("jwt") {
                intercept(ApplicationCallPipeline.Plugins) {
                    if (call.request.uri.contains("/falafel") && call.request.header("HX-Request") == null) {
                        val account = falafelAuth.getAccountBalanceOrNull(call)
                        call.respondFtl(
                            "/falafel/index",
                            "account" to account,
                            "assets" to assets,
                            "loadPage" to call.request.uri,
                        )
                        finish()
                    } else {
                        proceed()
                    }
                    get {
                        val account = falafelAuth.getAccountBalanceOrNull(call)
                        call.respondFtl(
                            "/falafel/index",
                            "account" to account,
                            "assets" to assets,
                            "loadPage" to null,
                        )
                    }
                    route("/me") {
                        get {
                            val account = falafelAuth.getAccountBalance(call)
                            call.respondFtl("/falafel/account/account-page", "account" to account)
                        }
                    }
                    resources.forEach { with(it) { routing() } }
                }
            }
        }
    }
}
