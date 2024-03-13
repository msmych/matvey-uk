package uk.matvey.migraine.frobot

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.UpdatesListener.CONFIRMED_UPDATES_ALL
import com.pengrad.telegrambot.request.AnswerCallbackQuery
import com.typesafe.config.Config
import mu.KotlinLogging
import uk.matvey.migraine.frobot.handlers.HandleMessageWithLowBattery
import uk.matvey.migraine.frobot.handlers.RockGardenJump
import uk.matvey.migraine.frobot.handlers.RockGardenStart
import uk.matvey.postal.Repo
import uk.matvey.postal.dataSource
import uk.matvey.telek.TgRequest

private val log = KotlinLogging.logger("frobot")

fun startFrobot(config: Config) {
    val bot = TelegramBot(config.getString("frobot.token"))
    val ds = dataSource(config)
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
    bot.setUpdatesListener { updates ->
        updates.forEach { update ->
            try {
                val rq = TgRequest(update)
                botUpdateHandler.handle(rq)
                if (rq.isCallbackQuery()) {
                    bot.execute(AnswerCallbackQuery(rq.callbackQueryId()))
                }
            } catch (e: Exception) {
                log.error(e) { "Oops" }
            }
        }
        CONFIRMED_UPDATES_ALL
    }
}
