package uk.matvey.begit.bot

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.Update
import uk.matvey.begit.club.ClubRepo
import uk.matvey.begit.club.ClubService
import uk.matvey.begit.member.MemberRepo

class UpdateProcessor(
    clubService: ClubService,
    clubRepo: ClubRepo,
    memberRepo: MemberRepo,
    bot: TelegramBot,
) {

    private val clubUpdateProcessor = ClubUpdateProcessor(clubService, clubRepo, memberRepo, bot)

    fun process(update: Update) {
        update.message()?.let { message ->
            val chat = message.chat()
            val chatId = chat.id()
            val title = chat.title()
            message.text()?.let { text ->
                if (text.startsWith("/club") && title != null) {
                    clubUpdateProcessor.greetClub(title, chatId)
                }
            }
        }
        update.callbackQuery()?.let { callbackQuery ->
            callbackQuery.data()?.let { data ->
                if (data.startsWith("/clubs")) {
                    clubUpdateProcessor.processCallback(update)
                }
            }
        }
    }
}