package uk.matvey.begit.bot

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.UpdatesListener.CONFIRMED_UPDATES_ALL
import com.typesafe.config.Config
import mu.KotlinLogging
import uk.matvey.begit.athlete.AthleteRepo
import uk.matvey.begit.athlete.AthleteUpdateProcessor
import uk.matvey.begit.club.ClubRepo
import uk.matvey.begit.club.ClubService
import uk.matvey.begit.event.EventRepo
import uk.matvey.begit.event.EventUpdateProcessor
import uk.matvey.slon.Repo

private val log = KotlinLogging.logger("startBot")

fun startBot(
    config: Config,
    repo: Repo,
    clubRepo: ClubRepo,
    eventRepo: EventRepo,
    athleteRepo: AthleteRepo,
    clubService: ClubService
) {
    val bot = TelegramBot(config.getString("tg.token"))
    val athleteUpdateProcessor = AthleteUpdateProcessor(repo, bot)
    val eventUpdateProcessor = EventUpdateProcessor(repo, eventRepo, bot)
    val clubUpdateProcessor = ClubUpdateProcessor(clubService, clubRepo, athleteRepo, bot)
    val updateProcessor = UpdateProcessor(repo, athleteUpdateProcessor, eventUpdateProcessor, clubUpdateProcessor, bot)
    bot.setUpdatesListener { updates ->
        updates.forEach { update ->
            try {
                updateProcessor.process(update)
            } catch (e: Exception) {
                log.warn(e) { "Failed to process update: $update" }
            }
        }
        CONFIRMED_UPDATES_ALL
    }
}