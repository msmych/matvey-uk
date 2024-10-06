package uk.matvey.falafel.title

import io.ktor.server.application.call
import io.ktor.server.auth.principal
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import uk.matvey.app.AccountPrincipal
import uk.matvey.falafel.balance.AccountBalance
import uk.matvey.falafel.balance.BalanceSql.ensureBalance
import uk.matvey.falafel.title.TitleSql.TITLES
import uk.matvey.slon.repo.Repo
import uk.matvey.slon.repo.RepoKit.insertOneInto
import uk.matvey.slon.value.Pg
import uk.matvey.utka.Resource
import uk.matvey.utka.ktor.KtorKit.receiveParamsMap
import uk.matvey.utka.ktor.ftl.FreeMarkerKit.respondFtl

class TitleResource(
    private val repo: Repo,
    private val titleService: TitleService,
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
            val titles = titleService.getTitles()
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
            val params = call.receiveParamsMap()
            repo.insertOneInto(TITLES) {
                set("state", Title.State.ACTIVE)
                set("title", params["title"])
                set("updated_at", Pg.now())
            }
            val titles = titleService.getTitles()
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
}
