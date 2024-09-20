package uk.matvey.app

import com.auth0.jwt.JWT
import io.ktor.server.application.call
import io.ktor.server.auth.AuthenticationContext
import io.ktor.server.auth.AuthenticationProvider
import io.ktor.server.response.respondRedirect
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import uk.matvey.app.account.Account
import uk.matvey.kit.string.StringKit.toUuid
import uk.matvey.kit.time.TimeKit.instant
import uk.matvey.utka.jwt.AuthJwt
import uk.matvey.utka.ktor.KtorKit.queryParam
import kotlin.time.Duration.Companion.days

class MatveyAuth(
    private val auth: AuthJwt,
) : AuthenticationProvider(object : Config("jwt") {}) {

    fun issueJwt(account: Account): String {
        val now = instant()
        return auth.issueJwt(
            expiration = 1.days,
            subject = account.id.toString(),
            issuedAt = now,
        ) {
            withClaim("name", account.name)
            withClaim("tags", account.tags.map { it.name })
        }
    }

    override suspend fun onAuthenticate(context: AuthenticationContext) {
        context.call.request.cookies["token"]?.let { JWT.decode(it) }?.let { token ->
            context.principal(AccountPrincipal(token.subject.toUuid(), token.getClaim("name").asString()))
        }
    }

    fun Route.authRouting() {
        get("/auth") {
            val token = call.queryParam("token")
            call.response.cookies.append("token", token)
            call.respondRedirect("/")
        }
    }
}
