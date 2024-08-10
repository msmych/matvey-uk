package uk.matvey.migraine.frobot.handlers

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup
import com.pengrad.telegrambot.model.request.ParseMode.MarkdownV2
import com.pengrad.telegrambot.request.EditMessageReplyMarkup
import com.pengrad.telegrambot.request.EditMessageText
import com.pengrad.telegrambot.request.SendMessage
import uk.matvey.migraine.frobot.Frobot
import uk.matvey.migraine.frobot.FrobotRepo
import uk.matvey.migraine.frobot.RockGardenBoard
import uk.matvey.telek.TgRequest
import java.util.UUID

class RockGardenStart(
    private val frobotRepo: FrobotRepo,
    private val bot: TelegramBot,
) {
    
    suspend operator fun invoke(rq: TgRequest, frobotId: UUID) {
        val frobot = frobotRepo.get(frobotId)
        frobot.tg.rockGarden?.messageId?.let { messageId ->
            bot.execute(EditMessageReplyMarkup(rq.userId(), messageId).replyMarkup(InlineKeyboardMarkup()))
            bot.execute(EditMessageText(rq.userId(), messageId, "🧯"))
        }
        val initialBoard = RockGardenBoard.fromString(
            """
                                brrrrrrr
                                rrrrrrrr
                                rrrrrrrr
                                rrrrrrrr
                                rrrrrrrr
                                rrrrrrrr
                                rrrrrrrr
                                rrrrrrrr
                            """.trimIndent().replace("\n", "")
        )
        val result = bot.execute(
            SendMessage(rq.userId(), "🐸 Wow, what a beautiful rock garden\\!")
                .replyMarkup(initialBoard.toInlineKeyboard()).parseMode(MarkdownV2)
        )
        frobotRepo.update(
            frobot.copy(
                tg = frobot.tg.copy(
                    rockGarden = Frobot.RockGarden(
                        messageId = result.message().messageId(),
                        board = initialBoard.serialize()
                    )
                )
            )
        )
    }
}
