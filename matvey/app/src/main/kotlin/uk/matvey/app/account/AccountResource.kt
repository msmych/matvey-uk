package uk.matvey.app.account

import io.ktor.http.HttpStatusCode.Companion.Unauthorized
import io.ktor.server.application.call
import io.ktor.server.auth.principal
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.patch
import io.ktor.server.routing.route
import uk.matvey.app.MatveyAuth
import uk.matvey.app.AccountPrincipal
import uk.matvey.app.account.AccountSql.ACCOUNTS
import uk.matvey.app.account.AccountSql.NAME
import uk.matvey.app.account.AccountSql.getAccountById
import uk.matvey.kit.string.StringKit.toUuid
import uk.matvey.slon.query.UpdateQueryBuilder.Companion.update
import uk.matvey.slon.repo.Repo
import uk.matvey.slon.value.PgUuid.Companion.toPgUuid
import uk.matvey.utka.Resource
import uk.matvey.utka.ktor.KtorKit.pathParam
import uk.matvey.utka.ktor.KtorKit.receiveParamsMap
import uk.matvey.utka.ktor.ftl.FreeMarkerKit.respondFtl

class AccountResource(
    private val auth: MatveyAuth,
    private val repo: Repo,
) : Resource {

    override fun Route.routing() {
        route("/accounts") {
            get("/edit-details-form") {
                val principal = call.principal<AccountPrincipal>()
                call.respondFtl(
                    "account/edit-details-form",
                    "id" to principal?.id,
                    "name" to principal?.name
                )
            }
            patch("/{id}") {
                val principal = call.principal<AccountPrincipal>() ?: return@patch call.respond(Unauthorized)
                val params = call.receiveParamsMap()
                val newName = params.getValue("name")
                repo.access { a ->
                    a.execute(
                        update(ACCOUNTS) {
                            set(NAME, newName)
                            where("id = ?", principal.id.toPgUuid())
                        }
                    )
                }
                val account = repo.access { a -> a.getAccountById(call.pathParam("id").toUuid()) }
                call.response.cookies.append("token", auth.issueJwt(account))
                call.respondFtl("me", "name" to newName)
            }
        }
    }
}
