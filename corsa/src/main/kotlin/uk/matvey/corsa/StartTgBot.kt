package uk.matvey.corsa

import com.typesafe.config.Config
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uk.matvey.corsa.athlete.AthleteSql.ensureAthlete
import uk.matvey.slon.repo.Repo
import uk.matvey.telek.TgBot
import uk.matvey.utka.jwt.AuthJwt
import kotlin.time.Duration.Companion.hours

fun startTgBot(
    tgBotConfig: Config,
    serverConfig: Config,
    auth: AuthJwt,
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
                    val jwt = auth.issueJwt(
                        expiration = 1.hours,
                        subject = athlete.id.toString(),
                    ) {
                        withClaim("name", athlete.name)
                    }
                    val host = serverConfig.getString("host")
                    val port = serverConfig.getInt("port")
                    tgBot.sendMessage(tgUserId, "$host:$port/auth?token=$jwt")
                }
        }
    }
}
