package uk.matvey.falafel.title

import io.ktor.server.auth.principal
import io.ktor.server.html.respondHtml
import io.ktor.server.request.header
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.patch
import io.ktor.server.routing.route
import io.ktor.server.sse.sse
import io.ktor.sse.ServerSentEvent
import kotlinx.html.body
import kotlinx.html.button
import kotlinx.html.div
import kotlinx.html.html
import kotlinx.html.stream.createHTML
import uk.matvey.app.account.AccountPrincipal
import uk.matvey.falafel.FalafelAuth
import uk.matvey.falafel.FalafelFtl
import uk.matvey.falafel.balance.AccountBalance
import uk.matvey.falafel.balance.AccountSql.ensureAccount
import uk.matvey.falafel.club.ClubTitleSql.ensureClubTitle
import uk.matvey.falafel.club.ClubTitleSql.toggleSavedTitle
import uk.matvey.falafel.club.ClubTitleSql.toggleWatchedTitle
import uk.matvey.falafel.tag.TagFtl.TAGS_EMOJIS
import uk.matvey.falafel.tag.TagFtl.TagCount
import uk.matvey.falafel.tag.TagService
import uk.matvey.falafel.tag.TagSql.findAllTagsByTitleId
import uk.matvey.falafel.title.TitleHtml.titleDetails
import uk.matvey.falafel.title.TitleHtml.titlePoster
import uk.matvey.falafel.title.TitleHtml.titleSaved
import uk.matvey.falafel.title.TitleHtml.titleWatched
import uk.matvey.falafel.title.TitleSql.getTitle
import uk.matvey.falafel.title.TitleSql.searchActiveTitles
import uk.matvey.kit.string.StringKit.toUuid
import uk.matvey.slon.repo.Repo
import uk.matvey.utka.Resource
import uk.matvey.utka.ktor.KtorKit.pathParam
import uk.matvey.utka.ktor.KtorKit.queryParam
import uk.matvey.utka.ktor.ftl.FreeMarkerKit.respondFtl

class TitleResource(
    private val falafelAuth: FalafelAuth,
    private val falafelFtl: FalafelFtl,
    private val repo: Repo,
    private val tagService: TagService,
    private val titleEvents: TitleEvents,
) : Resource {

    override fun Route.routing() {
        route("/titles") {
            getTitlesPage()
            route("/search") {
                searchTitles()
            }
            route("/{id}") {
                getTitle()
                route("/tags-view") {
                    getTagsView()
                }
                route("/tags-edit") {
                    getTagsEdit()
                }
                route("/watched") {
                    toggleWatched()
                }
                route("/saved") {
                    toggleSaved()
                }
                route("/events") {
                    setupTitleEvents()
                }
            }
            getNewTitleForm()
        }
    }

    private fun Route.getTitlesPage() {
        get {
            if (call.request.header("HX-Request") != "true") {
                return@get falafelFtl.respondIndex(call, "/falafel/titles")
            }
            val accountBalance = falafelAuth.getAccountBalance(call)
            call.respondFtl("/falafel/titles/titles-page", "account" to accountBalance)
        }
    }

    private fun Route.searchTitles() {
        get {
            val query = call.queryParam("q")
            val titles = repo.searchActiveTitles(query)
            call.respondFtl("/falafel/titles/titles-list", "titles" to titles)
        }
    }

    private fun Route.getNewTitleForm() {
        get("/new-title-form") {
            call.respondFtl("/falafel/titles/new-title-form")
        }
    }

    private fun Route.getTitle() {
        get {
            val account = falafelAuth.getAccountBalance(call)
            val titleId = call.pathParam("id").toUuid()
            if (call.request.header("HX-Request") != "true") {
                return@get falafelFtl.respondIndex(call, "/falafel/titles/$titleId")
            }
            val title = repo.access { a -> a.getTitle(titleId) }
            val clubTitle = repo.access { a -> a.ensureClubTitle(account.accountId, title.id) }
            val tags = tagService.getTagsByTitleId(titleId)
            call.respondHtml {
                titleDetails(
                    title,
                    clubTitle,
                    tags.map { (name, count) -> TagCount(name, count, TAGS_EMOJIS.getValue(name)) },
                    account,
                )
            }
        }
    }

    private fun Route.getTagsView() {
        get {
            val account = requireNotNull(falafelAuth.getAccountBalance(call))
            val titleId = call.pathParam("id").toUuid()
            val tags = repo.findAllTagsByTitleId(titleId)
            call.respondFtl(
                "/falafel/tags/tags-view",
                "tags" to tags.map { (name, count) -> TagCount.from(name, count) },
                "account" to account
            )
        }
    }

    private fun Route.getTagsEdit() {
        get {
            val account = call.principal<AccountPrincipal>()?.let {
                val balance = repo.access { a -> a.ensureAccount(it.id) }
                AccountBalance.from(it, balance)
            }
            val titleId = call.pathParam("id").toUuid()
            val tags = tagService.getTagsByTitleId(titleId)
            call.respondFtl(
                "/falafel/tags/tags-edit",
                "account" to account,
                "titleId" to titleId,
                "tags" to tags.map { (name, count) -> TagCount(name, count, TAGS_EMOJIS.getValue(name)) },
            )
        }
    }

    private fun Route.toggleWatched() {
        patch {
            val account = falafelAuth.getAccountBalance(call)
            val titleId = call.pathParam("id").toUuid()
            val clubTitle = repo.access { a -> a.toggleWatchedTitle(account.accountId, titleId) }
            call.respondHtml {
                body {
                    titleWatched(titleId, clubTitle.watched)
                }
            }
        }
    }

    private fun Route.toggleSaved() {
        patch {
            val account = falafelAuth.getAccountBalance(call)
            val titleId = call.pathParam("id").toUuid()
            val clubTitle = repo.access { a -> a.toggleSavedTitle(account.accountId, titleId) }
            call.respondHtml {
                body {
                    titleSaved(titleId, clubTitle.saved)
                }
            }
        }
    }

    private fun Route.setupTitleEvents() {
        sse {
            val titleId = call.pathParam("id").toUuid()
            val location = call.request.local.remoteHost + ":" + call.request.local.remotePort
            titleEvents.register(titleId, location)
            titleEvents.onEvent(titleId, location) {
                val title = repo.access { a -> a.getTitle(titleId) }
                val tags = tagService.getTagsByTitleId(titleId)
                val account = falafelAuth.getAccountBalance(call)
                send(
                    ServerSentEvent(
                        createHTML().html {
                            body {
                                title.refs.tmdbPosterPath?.let {
                                    titlePoster(it)
                                }
                                div(classes = "col gap-16") {
                                    div(classes = "t1") {
                                        +title.title
                                    }
                                    title.releaseYear?.let {
                                        div(classes = "t3") {
                                            +"Year: $it"
                                        }
                                    }
                                    title.directorName?.let {
                                        div(classes = "t3") {
                                            +"Director: $it"
                                        }
                                    }
                                    div {
                                        tags.forEach { (tagName, count) ->
                                            button {
                                                attributes["hx-post"] = "/falafel/tags/$tagName?titleId=${title.id}"
                                                attributes["hx-swap"] = "none"
                                                disabled = account.currentBalance <= 0
                                                +"${TAGS_EMOJIS[tagName]}"
                                                if (count > 0) {
                                                    +"$count"
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    )
                )
            }
        }
    }
}
