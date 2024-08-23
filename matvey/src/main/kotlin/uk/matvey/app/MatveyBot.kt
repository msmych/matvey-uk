package uk.matvey.app

import com.typesafe.config.Config
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import uk.matvey.app.account.Account
import uk.matvey.app.account.AccountSql.ensureAccount
import uk.matvey.app.account.AccountSql.getAccountByTgUserId
import uk.matvey.app.account.AccountSql.updateAccountStatus
import uk.matvey.slon.repo.Repo
import uk.matvey.telek.TgBot
import uk.matvey.telek.TgInlineKeyboardButton.Companion.callbackData
import uk.matvey.telek.TgUpdate

class MatveyBot(
    config: Config,
    private val repo: Repo,
) {
    private val bot = TgBot(config.getString("botToken"), config.getInt("longPollingSeconds"))
    private val adminGroupId = config.getLong("adminGroupId")

    fun start(): Job {
        return CoroutineScope(Dispatchers.IO).launch {
            bot.start { update ->
                processUpdate(update)
            }
        }
    }

    private suspend fun processStartCommand(update: TgUpdate) {
        val from = update.message().from()
        val tgUserId = from.id
        val account = repo.access { a -> a.ensureAccount(from.firstName, tgUserId) }
        when (account.state) {
            Account.State.PENDING -> {
                bot.sendMessage(
                    chatId = adminGroupId,
                    text = "Signup request from ${account.name}",
                    inlineKeyboard = listOf(
                        listOf(callbackData("✅ Approve", "accounts/$tgUserId/approve")),
                        listOf(callbackData("❌ Reject", "accounts/$tgUserId/reject"))
                    )
                )
                bot.sendMessage(
                    tgUserId,
                    """
                        |Hello, ${account.name}! Your signup request is submitted.
                        |I'll notify you when it's approved.
                    """.trimMargin()
                )
            }
            Account.State.ACTIVE -> bot.sendMessage(tgUserId, "👋")
            Account.State.DISABLED -> {}
        }
    }

    private suspend fun processUpdate(update: TgUpdate) {
        if (update.message?.text == "/start") {
            processStartCommand(update)
            return
        }
        if (update.callbackQuery?.data?.startsWith("accounts/") == true) {
            val callbackQuery = update.callbackQuery()
            val parts = callbackQuery.data!!.split("/")
            val tgUserId = parts[1].toLong()
            when (parts[2]) {
                "approve" -> {
                    repo.access { a ->
                        val account = a.getAccountByTgUserId(tgUserId)
                        a.updateAccountStatus(account.id, Account.State.ACTIVE)
                    }
                    bot.sendMessage(tgUserId, "Your signup request is approved")
                    bot.updateMessageInlineKeyboard(callbackQuery.message, listOf())
                }
                "reject" -> {
                    repo.access { a ->
                        val account = a.getAccountByTgUserId(tgUserId)
                        a.updateAccountStatus(account.id, Account.State.DISABLED)
                    }
                }
            }
            bot.answerCallbackQuery(callbackQuery.id)
            return
        }
    }
}
