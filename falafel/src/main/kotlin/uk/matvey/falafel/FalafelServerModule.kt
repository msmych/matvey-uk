package uk.matvey.falafel

import com.typesafe.config.Config
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.authenticate
import io.ktor.server.request.header
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.ktor.server.sse.SSE
import io.ktor.server.sse.sse
import io.ktor.sse.ServerSentEvent
import uk.matvey.falafel.balance.BalanceEvents
import uk.matvey.falafel.club.ClubResource
import uk.matvey.falafel.club.ClubService
import uk.matvey.falafel.tag.TagResource
import uk.matvey.falafel.tag.TagService
import uk.matvey.falafel.title.TitleEvents
import uk.matvey.falafel.title.TitleResource
import uk.matvey.falafel.tmdb.TmdbResource
import uk.matvey.slon.repo.Repo
import uk.matvey.tmdb.TmdbClient
import uk.matvey.utka.ktor.ftl.FreeMarkerKit.respondFtl

fun Application.falafelServerModule(
    serverConfig: Config,
    falafelAuth: FalafelAuth,
    repo: Repo,
    tmdbClient: TmdbClient,
    balanceEvents: BalanceEvents,
) {
    val clubService = ClubService(repo)
    val tagService = TagService(repo)
    val titleEvents = TitleEvents()
    val ftl = FalafelFtl(serverConfig, falafelAuth)
    val resources = listOf(
        ClubResource(repo, clubService),
        TitleResource(falafelAuth, ftl, repo, tagService, titleEvents),
        TagResource(falafelAuth, ftl, repo, tagService, titleEvents, balanceEvents),
        TmdbResource(falafelAuth, ftl, tmdbClient, repo),
    )
    install(SSE)
    routing {
        route("/falafel") {
            authenticate("jwt") {
                get {
                    ftl.respondIndex(call, "/falafel/titles")
                }
                route("/me") {
                    get {
                        if (call.request.header("HX-Request") != "true") {
                            return@get ftl.respondIndex(call, "/falafel/me")
                        }
                        val account = falafelAuth.getAccountBalance(call)
                        call.respondFtl("/falafel/account/account-page", "account" to account)
                    }
                    route("/events") {
                        sse {
                            val account = falafelAuth.getAccountBalance(call)
                            val location = call.request.local.remoteHost + ":" + call.request.local.remotePort
                            balanceEvents.register(account.accountId, location)
                            balanceEvents.onEvent(account.accountId, location) { balance ->
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
