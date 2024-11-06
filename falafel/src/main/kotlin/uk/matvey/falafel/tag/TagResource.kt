package uk.matvey.falafel.tag

import io.ktor.http.HttpStatusCode.Companion.Unauthorized
import io.ktor.server.auth.principal
import io.ktor.server.request.header
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import kotlinx.coroutines.flow.MutableSharedFlow
import uk.matvey.app.account.AccountPrincipal
import uk.matvey.falafel.FalafelAuth
import uk.matvey.falafel.FalafelFtl
import uk.matvey.falafel.balance.AccountBalance
import uk.matvey.falafel.balance.BalanceSql.ensureBalance
import uk.matvey.falafel.tag.TagFtl.TAGS_EMOJIS
import uk.matvey.falafel.tag.TagFtl.TagCount
import uk.matvey.falafel.tag.TagSql.addTagToTitle
import uk.matvey.falafel.title.TitleSql.getTitle
import uk.matvey.kit.string.StringKit.toUuid
import uk.matvey.slon.repo.Repo
import uk.matvey.utka.Resource
import uk.matvey.utka.ktor.KtorKit.pathParam
import uk.matvey.utka.ktor.KtorKit.queryParam
import uk.matvey.utka.ktor.ftl.FreeMarkerKit.respondFtl
import java.util.UUID

class TagResource(
    private val falafelAuth: FalafelAuth,
    private val falafelFtl: FalafelFtl,
    private val repo: Repo,
    private val tagService: TagService,
    private val titlesEvents: MutableMap<UUID, MutableSharedFlow<String>>,
    private val balanceEvents: MutableMap<UUID, MutableSharedFlow<Int>>,
) : Resource {

    override fun Route.routing() {
        route("/tags") {
            getTagsPage()
            route("/{name}") {
                addTag()
            }
        }
    }

    private fun Route.getTagsPage() {
        get {
            if (call.request.header("HX-Request") != "true") {
                return@get falafelFtl.respondIndex(call, "/falafel/tags")
            }
            val account = falafelAuth.getAccountBalance(call)
            call.respondFtl("/falafel/tags/tags-page", "account" to account)
        }
    }

    private fun Route.addTag() {
        post {
            val principal = call.principal<AccountPrincipal>() ?: return@post call.respond(Unauthorized)
            val titleId = call.queryParam("titleId").toUuid()
            val tagName = call.pathParam("name")
            repo.access { a -> a.addTagToTitle(principal.id, tagName, titleId) }
            val tags = tagService.getTagsByTitleId(titleId)
            val account = AccountBalance.from(principal, repo.access { a -> a.ensureBalance(principal.id) })
            val title = repo.access { a -> a.getTitle(titleId) }
            call.respondFtl(
                "/falafel/tags/tags-edit",
                "account" to account,
                "titleId" to titleId,
                "tags" to tags.map { (name, count) -> TagCount(name, count, TAGS_EMOJIS.getValue(name)) },
            )
            titlesEvents[title.id]?.emit(tagName)
            balanceEvents[account.accountId]?.emit(account.currentBalance)
        }
    }
}
