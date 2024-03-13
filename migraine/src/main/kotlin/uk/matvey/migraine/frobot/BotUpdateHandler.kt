package uk.matvey.migraine.frobot

import mu.KotlinLogging
import uk.matvey.migraine.frobot.Frobot.State.ACTIVE
import uk.matvey.migraine.frobot.Frobot.State.BATTERY_LOW
import uk.matvey.migraine.frobot.Frobot.State.OVERHEATED
import uk.matvey.migraine.frobot.handlers.HandleMessageWithLowBattery
import uk.matvey.migraine.frobot.handlers.RockGardenJump
import uk.matvey.migraine.frobot.handlers.RockGardenStart
import uk.matvey.telek.TgRequest

class BotUpdateHandler(
    private val frobotRepo: FrobotRepo,
    
    private val handleMessageWithLowBattery: HandleMessageWithLowBattery,
    private val rockGardenStart: RockGardenStart,
    private val rockGardenJump: RockGardenJump,
) {
    
    private val log = KotlinLogging.logger {}
    
    fun handle(rq: TgRequest) {
        log.info { rq.update }
        val frobot = frobotRepo.findByTgUserId(rq.userId()) ?: frobotRepo.add(Frobot.frobot(rq.userId()))
            .run { requireNotNull(frobotRepo.findByTgUserId(rq.userId())) }
        when (frobot.state) {
            BATTERY_LOW -> handleMessageWithLowBattery(rq, frobot.id)
            ACTIVE -> {
                if (rq.command().first == "/jump") {
                    rockGardenStart(rq, frobot.id)
                } else {
                    rockGardenJump(rq, frobot.id)
                }
            }
            
            OVERHEATED -> {}
        }
    }
}
