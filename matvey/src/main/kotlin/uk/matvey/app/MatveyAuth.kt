package uk.matvey.app

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import uk.matvey.app.account.Account
import uk.matvey.kit.time.TimeKit.instant
import kotlin.time.Duration.Companion.days
import kotlin.time.toJavaDuration

class MatveyAuth(
    private val algorithm: Algorithm,
) {

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
}
