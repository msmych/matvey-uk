package uk.matvey.app

import com.typesafe.config.Config
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import uk.matvey.app.account.Account
import uk.matvey.app.account.AccountSql.ensureAccount
import uk.matvey.slon.repo.Repo
import uk.matvey.telek.TgBot
import uk.matvey.telek.TgInlineKeyboardButton.Companion.callbackData

class MatveyBot(
    config: Config,
    private val repo: Repo,
) {
    private val bot = TgBot(config.getString("botToken"), config.getInt("longPollingSeconds"))
    private val adminGroupId = config.getLong("adminGroupId")

    fun start(): Job {
        return CoroutineScope(Dispatchers.IO).launch {
            bot.start { update ->
                if (update.message?.text == "/start") {
                    val from = update.message().from()
                    val account = repo.access { a -> a.ensureAccount(from.firstName, from.id) }
                    when (account.state) {
                        Account.State.PENDING -> {
                            bot.sendMessage(
                                chatId = adminGroupId,
                                text = "Signup request from ${account.name}",
                                inlineKeyboard = listOf(
                                    listOf(callbackData("✅ Approve", "accounts/${from.id}/approve")),
                                    listOf(callbackData("❌ Reject", "accounts/${from.id}/reject"))
                                )
                            )
                            bot.sendMessage(
                                from.id,
                                """
                                    |Hello, ${account.name}! Your signup request is submitted.
                                    |I'll notify you when it's approved.
                                """.trimMargin()
                            )
                        }
                        Account.State.ACTIVE -> bot.sendMessage(from.id, "Ciao")
                    }
                }
            }
        }
    }
}
