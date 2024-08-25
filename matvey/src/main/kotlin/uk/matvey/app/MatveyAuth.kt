package uk.matvey.app

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.call
import io.ktor.server.auth.AuthenticationContext
import io.ktor.server.auth.AuthenticationProvider
import io.ktor.server.auth.Principal
import io.ktor.server.response.respondRedirect
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import uk.matvey.app.account.Account
import uk.matvey.kit.string.StringKit.toUuid
import uk.matvey.kit.time.TimeKit.instant
import uk.matvey.utka.ktor.KtorKit.queryParam
import java.time.Instant
import java.util.UUID
import kotlin.time.Duration.Companion.days
import kotlin.time.toJavaDuration

class MatveyAuth(
    private val algorithm: Algorithm,
) : AuthenticationProvider(object : Config("jwt") {}) {

    class AccountPrincipal(val id: UUID, val name: String) : Principal

    fun issueJwt(account: Account): String {
        val now = instant()
        return JWT.create()
            .withIssuer("matvey")
            .withSubject(account.id.toString())
            .withIssuedAt(now)
            .withExpiresAt(now.plus(1.days.toJavaDuration()))
            .withClaim("name", account.name)
            .withClaim("tags", account.tags.map { it.name })
            .sign(algorithm)
    }

    fun invalidateJwt(account: Account): String {
        return JWT.create()
            .withIssuer("matvey")
            .withSubject(account.id.toString())
            .withExpiresAt(Instant.MIN)
            .withClaim("name", account.name)
            .withClaim("tags", account.tags.map { it.name })
            .sign(algorithm)
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
