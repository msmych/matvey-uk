package uk.matvey.falafel

import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.principal
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import uk.matvey.app.AccountPrincipal
import uk.matvey.falafel.balance.AccountBalance
import uk.matvey.falafel.balance.BalanceSql.ensureBalance
import uk.matvey.falafel.club.ClubResource
import uk.matvey.falafel.club.ClubService
import uk.matvey.falafel.tag.TagResource
import uk.matvey.falafel.tag.TagService
import uk.matvey.falafel.title.TitleResource
import uk.matvey.falafel.title.TitleService
import uk.matvey.slon.repo.Repo
import uk.matvey.utka.ktor.ftl.FreeMarkerKit.respondFtl

fun Application.falafelServerModule(
    repo: Repo,
) {
    val clubService = ClubService(repo)
    val titleService = TitleService(repo)
    val tagService = TagService(repo)
    val resources = listOf(
        ClubResource(repo, clubService),
        TitleResource(repo, titleService),
        TagResource(repo, tagService),
    )
    routing {
        route("/falafel") {
            authenticate("jwt") {
                get {
                    val account = call.principal<AccountPrincipal>()?.let {
                        val balance = repo.access { a -> a.ensureBalance(it.id) }
                        AccountBalance.from(it, balance)
                    }
                    call.respondFtl("falafel/index", "account" to account)
                }
                resources.forEach { with(it) { routing() } }
            }
        }
    }
}
