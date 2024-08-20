package uk.matvey.corsa

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import io.ktor.http.HttpStatusCode.Companion.Unauthorized
import io.ktor.server.application.call
import io.ktor.server.auth.AuthenticationContext
import io.ktor.server.auth.AuthenticationProvider
import io.ktor.server.auth.Principal
import io.ktor.server.response.respond
import io.ktor.server.response.respondRedirect
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import uk.matvey.kit.string.StringKit.toUuid
import uk.matvey.voron.KtorKit.queryParam
import java.util.UUID

class ServerAuth(private val algorithm: Algorithm) {

    class AthletePrincipal(val id: UUID, val name: String) : Principal

    class Provider(private val algorithm: Algorithm) : AuthenticationProvider(Config) {

        object Config : AuthenticationProvider.Config("jwt")

        override suspend fun onAuthenticate(context: AuthenticationContext) {
            val token = context.call.request.cookies["token"] ?: return context.call.respond(Unauthorized)
            val decoded = JWT.decode(token)
            try {
                JWT.require(algorithm)
                    .withIssuer("corsa")
                    .build()
                    .verify(token)
            } catch (e: JWTVerificationException) {
                return context.call.respond(Unauthorized)
            }
            val principal = AthletePrincipal(
                decoded.subject.toUuid(),
                decoded.getClaim("name").asString(),
            )
            context.principal(principal)
        }
    }

    fun Route.authRouting() {
        get("/auth") {
            val token = call.queryParam("token")
            try {
                JWT.require(algorithm)
                    .withIssuer("corsa")
                    .build()
                    .verify(token)
            } catch (e: JWTVerificationException) {
                return@get call.respond(Unauthorized)
            }
            call.response.cookies.append("token", token)
            call.respondRedirect("/")
        }
    }
}
