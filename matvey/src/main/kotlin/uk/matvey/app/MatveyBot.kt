package uk.matvey.app

import com.typesafe.config.Config
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import mu.KotlinLogging
import uk.matvey.app.account.Account
import uk.matvey.app.account.AccountSql.ensureAccount
import uk.matvey.app.account.AccountSql.getAccountByTgUserId
import uk.matvey.app.account.AccountSql.updateAccountStatus
import uk.matvey.app.config.AppConfig.ServerConfig
import uk.matvey.slon.repo.Repo
import uk.matvey.telek.Bot
import uk.matvey.telek.ReplyMarkup.InlineKeyboardButton
import uk.matvey.telek.Update

class MatveyBot(
    tgConfig: Config,
    private val serverConfig: ServerConfig,
    private val repo: Repo,
    private val matveyAuth: MatveyAuth,
    private val profile: Profile,
) {

    private val log = KotlinLogging.logger {}

    private val bot = Bot(
        token = tgConfig.getString("botToken"),
        longPollingSeconds = tgConfig.getInt("longPollingSeconds"),
        onUpdatesRetrievalException = { e -> log.error(e) { "Failed to fetch updates" } },
        onUpdateProcessingException = { e -> log.error(e) { "Failed to process update" } },
    )

    private val adminGroupId = tgConfig.getLong("adminGroupId")

    fun start(): Job {
        return CoroutineScope(Dispatchers.IO).launch {
            bot.start { update ->
                processUpdate(update)
            }
        }
    }

    private suspend fun processUpdate(update: Update) {
        if (update.message?.text == "/start") {
            processStartCommand(update)
            return
        }
        if (update.message?.text == "/login") {
            processLoginCommand(update)
            return
        }
        if (update.callbackQuery?.data?.startsWith("accounts/") == true) {
            processAccountActions(update)
            return
        }
    }

    private suspend fun processStartCommand(update: Update) {
        val from = update.message().from()
        val tgUserId = from.id
        val account = repo.access { a -> a.ensureAccount(from.firstName, tgUserId) }
        when (account.state) {
            Account.State.PENDING -> {
                bot.sendMessage(
                    chatId = adminGroupId,
                    text = "Signup request from ${account.name}",
                    inlineKeyboard = listOf(
                        listOf(InlineKeyboardButton.data("✅ Approve", "accounts/$tgUserId/approve")),
                        listOf(InlineKeyboardButton.data("❌ Reject", "accounts/$tgUserId/reject"))
                    ),
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

    private suspend fun processLoginCommand(update: Update) {
        val tgUserId = update.message().from().id
        val account = repo.access { a -> a.getAccountByTgUserId(tgUserId) }
        if (account.state == Account.State.ACTIVE) {
            val token = matveyAuth.issueJwt(account)
            val url = "${serverConfig.url(profile)}/auth?token=$token"
            if (profile == Profile.PROD) {
                bot.sendMessage(
                    chatId = tgUserId,
                    text = "Login",
                    inlineKeyboard = listOf(listOf(InlineKeyboardButton.url("Go", url)))
                )
            } else {
                bot.sendMessage(chatId = tgUserId, text = url)
            }
        }
    }

    private suspend fun processAccountActions(update: Update) {
        val callbackQuery = update.callbackQuery()
        val parts = callbackQuery.data().split("/")
        val tgUserId = parts[1].toLong()
        when (parts[2]) {
            "approve" -> {
                repo.access { a ->
                    val account = a.getAccountByTgUserId(tgUserId)
                    a.updateAccountStatus(account.id, Account.State.ACTIVE)
                }
                bot.sendMessage(tgUserId, "Your signup request is approved")
            }
            "reject" -> {
                repo.access { a ->
                    val account = a.getAccountByTgUserId(tgUserId)
                    a.updateAccountStatus(account.id, Account.State.DISABLED)
                }
            }
        }
        bot.editMessage(callbackQuery.message(), inlineKeyboard = listOf())
        bot.answerCallbackQuery(callbackQuery.id)
    }
}
