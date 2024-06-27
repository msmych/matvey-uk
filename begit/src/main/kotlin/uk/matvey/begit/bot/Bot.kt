package uk.matvey.begit.bot

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.UpdatesListener.CONFIRMED_UPDATES_ALL
import com.typesafe.config.Config
import mu.KotlinLogging
import uk.matvey.begit.club.ClubRepo
import uk.matvey.begit.club.ClubService
import uk.matvey.begit.member.MemberRepo

private val log = KotlinLogging.logger("startBot")

fun startBot(
    config: Config,
    clubRepo: ClubRepo,
    memberRepo: MemberRepo,
    clubService: ClubService
) {
    val bot = TelegramBot(config.getString("tg.token"))
    val updateProcessor = UpdateProcessor(clubService, clubRepo, memberRepo, bot)
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