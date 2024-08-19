package uk.matvey.corsa

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.typesafe.config.Config
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uk.matvey.corsa.athlete.AthleteSql.ensureAthlete
import uk.matvey.slon.repo.Repo
import uk.matvey.telek.TgBot
import java.time.Instant

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
                    val now = Instant.now()
                    val jwt = JWT.create()
                        .withIssuer("corsa")
                        .withSubject("tg-$tgUserId")
                        .withIssuedAt(now)
                        .withExpiresAt(now.plusSeconds(600))
                        .sign(algorithm)
                    val host = serverConfig.getString("host")
                    val port = serverConfig.getInt("port")
                    repo.access { a -> a.ensureAthlete(tgUserId, from.firstName) }
                    tgBot.sendMessage(tgUserId, "$host:$port/auth?token=$jwt")
                }
        }
    }
}
