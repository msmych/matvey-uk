package uk.matvey.app

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
import uk.matvey.app.config.AppConfig.TgConfig
import uk.matvey.slon.repo.Repo
import uk.matvey.telek.Bot
import uk.matvey.telek.ReplyMarkup.InlineKeyboardButton
import uk.matvey.telek.Update

class MatveyBot(
    tgConfig: TgConfig,
    private val serverConfig: ServerConfig,
    private val repo: Repo,
    private val matveyAuth: MatveyAuth,
    private val profile: Profile,
) {

    private val log = KotlinLogging.logger {}

    private val bot = Bot(
        token = tgConfig.botToken(),
        longPollingSeconds = tgConfig.longPollingSeconds(),
        onUpdatesRetrievalException = { e -> log.error(e) { "Failed to fetch updates" } },
        onUpdateProcessingException = { e -> log.error(e) { "Failed to process update" } },
    )

    private val adminGroupId = tgConfig.adminGroupId()

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
        val user = update.message().from()
        val userId = user.id
        val account = repo.access { a -> a.ensureAccount(user.firstName, userId) }
        when (account.state) {
            Account.State.PENDING -> {
                bot.sendMessage(
                    chatId = adminGroupId,
                    text = "Signup request from ${account.name}",
                    inlineKeyboard = listOf(
                        listOf(InlineKeyboardButton.data("✅ Approve", "accounts/$userId/approve")),
                        listOf(InlineKeyboardButton.data("❌ Reject", "accounts/$userId/reject"))
                    ),
                )
                bot.sendMessage(
                    userId,
                    """
                        |Hello, ${account.name}! Your signup request is submitted.
                        |I'll notify you when it's approved.
                    """.trimMargin()
                )
            }
            Account.State.ACTIVE -> bot.sendMessage(userId, "👋")
            Account.State.DISABLED -> {}
        }
    }

    private suspend fun processLoginCommand(update: Update) {
        val userId = update.message().from().id
        val account = repo.access { a -> a.getAccountByTgUserId(userId) }
        if (account.state == Account.State.ACTIVE) {
            val token = matveyAuth.issueJwt(account)
            val url = "${serverConfig.url(profile)}/auth?token=$token"
            if (profile == Profile.PROD) {
                bot.sendMessage(
                    chatId = userId,
                    text = "Login",
                    inlineKeyboard = listOf(listOf(InlineKeyboardButton.url("Go", url)))
                )
            } else {
                bot.sendMessage(chatId = userId, text = url)
            }
        }
    }

    private suspend fun processAccountActions(update: Update) {
        val callbackQuery = update.callbackQuery()
        val parts = callbackQuery.data().split("/")
        val userId = parts[1].toLong()
        when (parts[2]) {
            "approve" -> {
                repo.access { a ->
                    val account = a.getAccountByTgUserId(userId)
                    a.updateAccountStatus(account.id, Account.State.ACTIVE)
                }
                bot.sendMessage(userId, "Your signup request is approved")
            }
            "reject" -> {
                repo.access { a ->
                    val account = a.getAccountByTgUserId(userId)
                    a.updateAccountStatus(account.id, Account.State.DISABLED)
                }
            }
        }
        bot.editMessage(callbackQuery.message(), inlineKeyboard = listOf())
        bot.answerCallbackQuery(callbackQuery.id)
    }
}
