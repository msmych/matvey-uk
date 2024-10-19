package uk.matvey.falafel.title

import io.ktor.server.auth.principal
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import uk.matvey.app.account.AccountPrincipal
import uk.matvey.falafel.FalafelAuth
import uk.matvey.falafel.balance.AccountBalance
import uk.matvey.falafel.balance.BalanceSql.ensureBalance
import uk.matvey.falafel.tag.TagFtl
import uk.matvey.falafel.tag.TagFtl.TagCount
import uk.matvey.falafel.tag.TagService
import uk.matvey.falafel.tag.TagSql.findAllTagsByTitleId
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
    private val repo: Repo,
    private val tagService: TagService,
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
            }
            getNewTitleForm()
        }
    }

    private fun Route.getTitlesPage() {
        get {
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
            val titleId = call.pathParam("id").toUuid()
            val title = repo.access { a -> a.getTitle(titleId) }
            call.respondFtl("/falafel/titles/title-details", "title" to title)
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
                val balance = repo.access { a -> a.ensureBalance(it.id) }
                AccountBalance.from(it, balance)
            }
            val titleId = call.pathParam("id").toUuid()
            val tags = tagService.getTagsByTitleId(titleId)
            call.respondFtl(
                "/falafel/tags/tags-edit",
                "account" to account,
                "titleId" to titleId,
                "tags" to tags.map { (name, count) -> TagCount(name, count, TagFtl.TAGS_EMOJIS.getValue(name)) },
            )
        }
    }
}
