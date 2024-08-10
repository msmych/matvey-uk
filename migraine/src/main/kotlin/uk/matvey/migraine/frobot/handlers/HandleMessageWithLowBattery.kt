package uk.matvey.migraine.frobot.handlers

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.request.SendMessage
import uk.matvey.migraine.frobot.Frobot.State.ACTIVE
import uk.matvey.migraine.frobot.FrobotRepo
import uk.matvey.telek.TgRequest
import java.util.UUID

class HandleMessageWithLowBattery(
    private val frobotRepo: FrobotRepo,
    private val bot: TelegramBot,
) {

    suspend operator fun invoke(rq: TgRequest, frobotId: UUID) {
        val text = rq.messageText()
        when (text) {
            in INSECTS -> {
                val frobot = frobotRepo.get(frobotId)
                frobotRepo.update(frobot.copy(state = ACTIVE))
                bot.execute(SendMessage(rq.userId(), "🐸 Yummy!"))
                bot.execute(SendMessage(rq.userId(), "🔋"))
            }
            in ELECTRICITY -> {
                bot.execute(SendMessage(rq.userId(), "🐸 Not tasty"))
                bot.execute(SendMessage(rq.userId(), "🪫"))
            }
            else -> {
                bot.execute(SendMessage(rq.userId(), "🪫"))
            }
        }
    }

    companion object {
        private val INSECTS = setOf("🦋", "🐝", "🐞", "🐜", "🦟", "🪰")

        private val ELECTRICITY = setOf("🔌", "⚡️")
    }
}
