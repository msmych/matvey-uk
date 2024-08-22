package uk.matvey.corsa

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.typesafe.config.Config
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uk.matvey.corsa.athlete.AthleteSql.ensureAthlete
import uk.matvey.kit.time.TimeKit.instant
import uk.matvey.slon.repo.Repo
import uk.matvey.telek.TgBot

fun startTgBot(
    tgBotConfig: Config,
    serverConfig: Config,
    algorithm: Algorithm,
    repo: Repo
) {
    CoroutineScope(Dispatchers.IO).launch {
        val tgBot = TgBot(tgBotConfig.getString("token"))
        tgBot.start { update ->
            update.message
                ?.takeIf { it.text == "/login" }
                ?.let {
                    val from = it.from()
                    val tgUserId = from.id
                    val athlete = repo.access { a -> a.ensureAthlete(tgUserId, from.firstName) }
                    val now = instant()
                    val jwt = JWT.create()
                        .withIssuer("corsa")
                        .withSubject(athlete.id.toString())
                        .withIssuedAt(now)
                        .withExpiresAt(now.plusSeconds(3600))
                        .withClaim("name", athlete.name)
                        .sign(algorithm)
                    val host = serverConfig.getString("host")
                    val port = serverConfig.getInt("port")
                    tgBot.sendMessage(tgUserId, "$host:$port/auth?token=$jwt")
                }
        }
    }
}
