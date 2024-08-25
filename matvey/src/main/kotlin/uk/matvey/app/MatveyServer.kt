package uk.matvey.app

import com.typesafe.config.Config
import io.ktor.http.HttpStatusCode.Companion.Unauthorized
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.principal
import io.ktor.server.engine.embeddedServer
import io.ktor.server.http.content.staticResources
import io.ktor.server.netty.Netty
import io.ktor.server.response.respond
import io.ktor.server.response.respondRedirect
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.patch
import io.ktor.server.routing.routing
import uk.matvey.app.MatveyAuth.AccountPrincipal
import uk.matvey.app.account.AccountSql
import uk.matvey.app.account.AccountSql.getAccountById
import uk.matvey.kit.string.StringKit.toUuid
import uk.matvey.slon.access.AccessKit.update
import uk.matvey.slon.param.TextParam.Companion.text
import uk.matvey.slon.param.UuidParam.Companion.uuid
import uk.matvey.slon.repo.Repo
import uk.matvey.utka.ktor.KtorKit.pathParam
import uk.matvey.utka.ktor.KtorKit.receiveParamsMap
import uk.matvey.utka.ktor.ftl.FreeMarkerKit.installFreeMarker
import uk.matvey.utka.ktor.ftl.FreeMarkerKit.respondFtl

fun startServer(
    serverConfig: Config,
    profile: Profile,
    auth: MatveyAuth,
    repo: Repo,
) {
    embeddedServer(
        factory = Netty,
        port = serverConfig.getInt("port"),
        watchPaths = when (profile) {
            Profile.LOCAL,
            Profile.TEST -> listOf("resources", "classes")
            else -> listOf()
        },
        module = {
            installFreeMarker("templates")
            install(Authentication) {
                register(auth)
            }
            routing {
                staticResources("/assets", "/assets")
                get("/health") {
                    call.respondText("OK")
                }
                get("/login") {
                    call.respondFtl("login")
                }
                authenticate("jwt") {
                    get("/me") {
                        val principal = call.principal<AccountPrincipal>()
                        call.respondFtl("me", "name" to principal?.name)
                    }
                    get("/accounts/edit-details-form") {
                        val principal = call.principal<AccountPrincipal>()
                        call.respondFtl("account/edit-details-form", "id" to principal?.id, "name" to principal?.name)
                    }
                    patch("/accounts/{id}") {
                        val principal = call.principal<AccountPrincipal>() ?: return@patch call.respond(Unauthorized)
                        val params = call.receiveParamsMap()
                        val newName = params.getValue("name")
                        repo.access { a ->
                            a.update(AccountSql.ACCOUNTS) {
                                set(AccountSql.NAME to text(newName))
                                where("id = ?", uuid(principal.id))
                            }
                        }
                        val account = repo.access { a -> a.getAccountById(call.pathParam("id").toUuid()) }
                        call.response.cookies.append("token", auth.issueJwt(account))
                        call.respondFtl("me", "name" to newName)
                    }
                    get("/logout") {
                        val account = repo.access { a -> a.getAccountById(call.principal<AccountPrincipal>()!!.id) }
                        call.response.cookies.append("token", auth.invalidateJwt(account))
                        call.respondRedirect("/")
                    }
                }
                authenticate("jwt") {
                    get("/") {
                        val principal = call.principal<AccountPrincipal>()
                        call.respondFtl("index", "account" to principal)
                    }
                }
                with(auth) { authRouting() }
            }
        },
    )
        .start(wait = true)
}
