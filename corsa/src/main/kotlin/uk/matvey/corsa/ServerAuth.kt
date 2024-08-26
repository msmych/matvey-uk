package uk.matvey.corsa

import com.auth0.jwt.JWT
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
import uk.matvey.utka.jwt.AuthJwt
import uk.matvey.utka.ktor.KtorKit.queryParam
import java.util.UUID

class ServerAuth(private val auth: AuthJwt) {

    class AthletePrincipal(val id: UUID, val name: String) : Principal

    class Provider(private val authJwt: AuthJwt) : AuthenticationProvider(Config) {

        object Config : AuthenticationProvider.Config("jwt")

        override suspend fun onAuthenticate(context: AuthenticationContext) {
            val token = context.call.request.cookies["token"] ?: return context.call.respond(Unauthorized)
            if (!authJwt.validateJwt(token).isValid()) {
                return context.call.respond(Unauthorized)
            }
            val decoded = JWT.decode(token)
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
            if (!auth.validateJwt(token).isValid()) {
                return@get call.respond(Unauthorized)
            }
            call.response.cookies.append("token", token)
            call.respondRedirect("/")
        }
    }
}
