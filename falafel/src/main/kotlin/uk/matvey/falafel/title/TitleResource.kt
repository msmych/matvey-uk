package uk.matvey.falafel.title

import io.ktor.http.HttpStatusCode.Companion.BadRequest
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
import uk.matvey.falafel.title.TitleSql.addTitle
import uk.matvey.falafel.title.TitleSql.getActiveTitles
import uk.matvey.slon.repo.Repo
import uk.matvey.utka.Resource
import uk.matvey.utka.ktor.KtorKit.receiveParamsMap
import uk.matvey.utka.ktor.ftl.FreeMarkerKit.respondFtl

class TitleResource(
    private val repo: Repo,
) : Resource {

    override fun Route.routing() {
        route("/titles") {
            getTitles()
            getNewTitleForm()
            addTitle()
        }
    }

    private fun Route.getTitles() {
        get {
            val titles = repo.getActiveTitles()
            val account = call.principal<AccountPrincipal>()?.let {
                val balance = repo.access { a -> a.ensureBalance(it.id) }
                AccountBalance.from(it, balance)
            }
            call.respondFtl(
                "/falafel/titles/titles",
                "titles" to titles,
                "account" to account,
            )
        }
    }

    private fun Route.getNewTitleForm() {
        get("/new-title-form") {
            call.respondFtl("/falafel/titles/new-title-form")
        }
    }

    private fun Route.addTitle() {
        post {
            val account = call.principal<AccountPrincipal>()?.let {
                val balance = repo.access { a -> a.ensureBalance(it.id) }
                AccountBalance.from(it, balance)
            } ?: return@post call.respond(BadRequest)
            val params = call.receiveParamsMap()
            val state = if (account.isAdmin()) { Title.State.ACTIVE } else { Title.State.PENDING }
            repo.access { a -> a.addTitle(state, params.getValue("title")) }
            val titles = repo.getActiveTitles()
            call.respondFtl(
                "/falafel/titles/titles",
                "titles" to titles,
                "account" to account,
            )
        }
    }
}
