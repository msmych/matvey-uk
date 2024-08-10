package uk.matvey.migraine.frobot

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.UpdatesListener.CONFIRMED_UPDATES_ALL
import com.typesafe.config.Config
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import mu.KotlinLogging
import uk.matvey.migraine.frobot.handlers.HandleMessageWithLowBattery
import uk.matvey.migraine.frobot.handlers.RockGardenJump
import uk.matvey.migraine.frobot.handlers.RockGardenStart
import uk.matvey.slon.DataSourceKit.hikariDataSource
import uk.matvey.slon.FlywayKit.flywayConfig
import uk.matvey.slon.repo.Repo
import uk.matvey.telek.TgExecuteSupport.answerCallbackQuery
import uk.matvey.telek.TgRequest
import javax.sql.DataSource

private val log = KotlinLogging.logger("frobot")

@OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
fun startFrobot(config: Config) {
    uk.matvey.slon.DataSourceKit
    val bot = TelegramBot(config.getString("frobot.token"))
    val ds = dataSource(config.getConfig("ds"))
    migrate(ds, System.getenv("CLEAN_DB") == "true")
    val repo = Repo(ds)
    val frobotRepo = FrobotRepo(repo)
    val handleMessageWithLowBattery = HandleMessageWithLowBattery(frobotRepo, bot)
    val rockGardenStart = RockGardenStart(frobotRepo, bot)
    val rockGardenJump = RockGardenJump(frobotRepo, bot)
    val botUpdateHandler = BotUpdateHandler(
        frobotRepo,
        handleMessageWithLowBattery,
        rockGardenStart,
        rockGardenJump
    )
    CoroutineScope(newSingleThreadContext("bot")).launch {
        bot.setUpdatesListener { updates ->
            updates.forEach { update ->
                try {
                    val rq = TgRequest(update)
                    launch {
                        botUpdateHandler.handle(rq)
                    }
                    if (rq.isCallbackQuery()) {
                        bot.answerCallbackQuery(rq.callbackQueryId())
                    }
                } catch (e: Exception) {
                    log.error(e) { "Oops" }
                }
            }
            CONFIRMED_UPDATES_ALL
        }
    }
}

fun migrate(ds: DataSource, clean: Boolean) {
    val flyway = flywayConfig(
        dataSource = ds,
        schema = "migraine",
        location = "classpath:db/migration/migraine",
        createSchema = true,
        cleanDisabled = !clean,
    ).load()
    if (clean) {
        flyway.clean()
    }
    flyway.migrate()
}

private fun dataSource(config: Config): DataSource {
    return hikariDataSource(
        config.getString("jdbcUrl"),
        config.getString("username"),
        config.getString("password"),
    )
}
