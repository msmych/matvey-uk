package uk.matvey.migraine.frobot.handlers

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.request.ParseMode.MarkdownV2
import com.pengrad.telegrambot.request.AnswerCallbackQuery
import com.pengrad.telegrambot.request.EditMessageReplyMarkup
import com.pengrad.telegrambot.request.EditMessageText
import com.pengrad.telegrambot.request.SendMessage
import uk.matvey.migraine.frobot.Frobot.State.OVERHEATED
import uk.matvey.migraine.frobot.FrobotRepo
import uk.matvey.migraine.frobot.RockGardenCell.TreasureMap
import uk.matvey.telek.TgRequest
import java.util.UUID
import java.util.concurrent.ThreadLocalRandom

class RockGardenJump(
    private val frobotRepo: FrobotRepo,
    private val bot: TelegramBot,
) {
    
    operator fun invoke(rq: TgRequest, frobotId: UUID) {
        val (i, j) = rq.callbackQueryData().let { it[0].digitToInt() to it[1].digitToInt() }
        val frobot = frobotRepo.get(frobotId)
        if (frobot.rockGardenBoard().cellAt(i, j) is TreasureMap && frobot.rockGardenBoard().isReachableRock(i, j)) {
            frobotRepo.update(frobot.copy(state = OVERHEATED))
            bot.execute(SendMessage(rq.userId(), "☠️ *OVERHEATED*").parseMode(MarkdownV2))
            bot.execute(SendMessage(rq.userId(), "☠️ *ALL SYSTEMS DOWN*").parseMode(MarkdownV2))
            bot.execute(SendMessage(rq.userId(), "🤖 JUNK Robotics™®©: rescue team is on its way"))
            bot.execute(SendMessage(rq.userId(), "🔵🔵🔴🟢"))
        } else {
            val updatedBoard = frobot.rockGardenBoard().move(i, j)
            if (updatedBoard != frobot.rockGardenBoard()) {
                frobotRepo.update(frobot.copy(tg = frobot.tg.copy(rockGarden = frobot.tg.rockGarden?.copy(board = updatedBoard.serialize()))))
                bot.execute(
                    EditMessageReplyMarkup(rq.userId(), rq.messageId())
                        .replyMarkup(updatedBoard.toInlineKeyboard())
                )
                when (updatedBoard.serialize().count { it == 'f' }) {
                    0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11 -> null
                    12 -> " Hmm, starting to feel a little toasty in here"
                    28 -> "❗️ Okay, this is getting seriously hot"
                    48 -> "❗️ Oh man, I'm burning up"
                    56 -> "⚠️ Language module квакнулся. 당신은 마주칠 수도 있습니다 alcuni problemi स्थानीयकरण के"
                    60 -> "‼️️ Danger ‼️ Critical overheat"
                    62 -> " Oh look! There's a map over there!"
                    else -> "⚠️ ${NULL_POINTER_MESSAGES.random()}"
                        .takeIf { ThreadLocalRandom.current().nextInt() % 24 == 0 }
                }?.let { logMessage ->
                    bot.execute(
                        EditMessageText(
                            rq.userId(), rq.messageId(),
                            "${rq.messageText()}\n🐸$logMessage".replace("!", "\\!").replace(".", "\\.")
                        )
                            .replyMarkup(updatedBoard.toInlineKeyboard())
                            .parseMode(MarkdownV2)
                    )
                }
            }
            bot.execute(AnswerCallbackQuery(rq.callbackQueryId()))
        }
    }
    
    companion object {
        
        val NULL_POINTER_MESSAGES = setOf(
            "Epic Null Pointer Fail",
            "Null Pointer Annihilation",
            "Null Pointer Catastrophe",
            "Null Pointer Collapse",
            "Null Pointer Death",
            "Null Pointer Disaster",
            "Null Pointer Explosion",
            "Null Pointer Fiasco",
            "Null Pointer Horror",
            "Null Pointer Misery",
            "Null Pointer Misfortune",
            "Null Pointer Tragedy",
            "Null Pointer Trouble",
        )
    }
}
