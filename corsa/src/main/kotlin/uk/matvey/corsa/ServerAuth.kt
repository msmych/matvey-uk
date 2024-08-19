package uk.matvey.corsa

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import io.ktor.http.HttpStatusCode.Companion.Unauthorized
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.response.respondRedirect
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import uk.matvey.voron.KtorKit.queryParam

class ServerAuth(private val algorithm: Algorithm) {

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
