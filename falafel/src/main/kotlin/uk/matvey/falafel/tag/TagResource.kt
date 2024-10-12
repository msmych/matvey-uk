package uk.matvey.falafel.tag

import io.ktor.http.HttpStatusCode.Companion.Unauthorized
import io.ktor.server.application.call
import io.ktor.server.auth.principal
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import uk.matvey.app.account.AccountPrincipal
import uk.matvey.falafel.balance.AccountBalance
import uk.matvey.falafel.balance.BalanceSql.ensureBalance
import uk.matvey.falafel.tag.TagSql.addTagToTitle
import uk.matvey.kit.string.StringKit.toUuid
import uk.matvey.slon.repo.Repo
import uk.matvey.utka.Resource
import uk.matvey.utka.ktor.KtorKit.pathParam
import uk.matvey.utka.ktor.KtorKit.queryParam
import uk.matvey.utka.ktor.ftl.FreeMarkerKit.respondFtl

class TagResource(private val repo: Repo, private val tagService: TagService) : Resource {

    override fun Route.routing() {
        route("/tags") {
            getTags()
            route("/{name}") {
                addTag()
            }
        }
    }

    data class TagCount(
        val name: String,
        val count: Int,
        val emoji: String,
    )

    private fun Route.getTags() {
        get {
            val account = call.principal<AccountPrincipal>()?.let {
                val balance = repo.access { a -> a.ensureBalance(it.id) }
                AccountBalance.from(it, balance)
            }
            val titleId = call.queryParam("titleId").toUuid()
            val tags = tagService.getTagsByTitleId(titleId)
            call.respondFtl(
                "/falafel/tags/tags",
                "account" to account,
                "titleId" to titleId,
                "tags" to tags.map { (name, count) -> TagCount(name, count, Tags.TAGS_EMOJIS.getValue(name)) },
            )
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
            call.respondFtl(
                "/falafel/tags/tags",
                "account" to account,
                "titleId" to titleId,
                "tags" to tags.map { (name, count) -> TagCount(name, count, Tags.TAGS_EMOJIS.getValue(name)) },
            )
        }
    }
}
