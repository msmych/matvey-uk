package uk.matvey.falafel

import com.typesafe.config.Config
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.authenticate
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.ktor.server.sse.SSE
import io.ktor.server.sse.sse
import io.ktor.sse.ServerSentEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import mu.KotlinLogging
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

private val log = KotlinLogging.logger { }

fun Application.falafelServerModule(
    serverConfig: Config,
    falafelAuth: FalafelAuth,
    repo: Repo,
    tmdbClient: TmdbClient,
    balanceEvents: MutableMap<UUID, MutableSharedFlow<Int>>,
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
                get {
                    val account = falafelAuth.getAccountBalanceOrNull(call)
                    call.respondFtl(
                        "/falafel/index",
                        "account" to account,
                        "assets" to assets,
                        "loadPage" to "/falafel/titles",
                    )
                }
                route("/me") {
                    get {
                        val account = falafelAuth.getAccountBalance(call)
                        call.respondFtl("/falafel/account/account-page", "account" to account)
                    }
                    route("/events") {
                        sse {
                            val account = falafelAuth.getAccountBalance(call)
                            val events = MutableSharedFlow<Int>()
                            balanceEvents[account.accountId] = events
                            events.collect { balance ->
                                send(ServerSentEvent(balance.toString()))
                            }
                        }
                    }
                }
                resources.forEach { with(it) { routing() } }
            }
        }
    }
}
