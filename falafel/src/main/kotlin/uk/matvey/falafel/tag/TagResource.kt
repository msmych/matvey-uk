package uk.matvey.falafel.tag

import io.ktor.http.HttpStatusCode.Companion.Unauthorized
import io.ktor.server.application.call
import io.ktor.server.auth.principal
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import uk.matvey.falafel.FalafelAuth.FalafelPrincipal
import uk.matvey.falafel.balance.BalanceSql
import uk.matvey.kit.string.StringKit.toUuid
import uk.matvey.slon.access.AccessKit.insertOneInto
import uk.matvey.slon.access.AccessKit.update
import uk.matvey.slon.repo.Repo
import uk.matvey.slon.value.Pg
import uk.matvey.slon.value.PgUuid.Companion.toPgUuid
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
            val titleId = call.queryParam("titleId").toUuid()
            val tags = tagService.getTagsByTitleId(titleId)
            call.respondFtl(
                "/falafel/tags/tags",
                "titleId" to titleId,
                "tags" to tags.map { (name, count) -> TagCount(name, count, Tags.TAGS_EMOJIS.getValue(name)) },
            )
        }
    }

    private fun Route.addTag() {
        post {
            val account = call.principal<FalafelPrincipal>() ?: return@post call.respond(Unauthorized)
            val titleId = call.queryParam("titleId").toUuid()
            val tagName = call.pathParam("name")
            repo.access { a ->
                a.update(BalanceSql.BALANCES) {
                    set(BalanceSql.CURRENT, Pg.plain("${BalanceSql.CURRENT} - 1"))
                    where("${BalanceSql.ACCOUNT_ID} = ? and ${BalanceSql.CURRENT} > 0", account.accountId.toPgUuid())
                }
                a.insertOneInto(TagSql.TAGS) {
                    set(TagSql.NAME, tagName)
                    set(TagSql.TITLE_ID, titleId)
                }
            }
            val tags = tagService.getTagsByTitleId(titleId)
            call.respondFtl(
                "/falafel/tags/tags",
                "titleId" to titleId,
                "tags" to tags.map { (name, count) -> TagCount(name, count, Tags.TAGS_EMOJIS.getValue(name)) },
            )
        }
    }
}
