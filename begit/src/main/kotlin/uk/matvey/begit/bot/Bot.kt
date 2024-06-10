package uk.matvey.begit.bot

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.UpdatesListener.CONFIRMED_UPDATES_ALL
import com.typesafe.config.Config
import uk.matvey.begit.club.ClubService

fun startBot(
    config: Config,
    clubService: ClubService
) {
    val bot = TelegramBot(config.getString("tg.token"))
    val updateProcessor = UpdateProcessor(clubService, bot)
    bot.setUpdatesListener { updates ->
        updates.forEach { update ->
            updateProcessor.process(update)
        }
        CONFIRMED_UPDATES_ALL
    }
}