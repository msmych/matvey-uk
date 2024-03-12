package uk.matvey.migraine.frobot

import mu.KotlinLogging
import uk.matvey.migraine.frobot.handlers.HandleMessageWithLowBattery
import uk.matvey.telek.TgRequest

class BotUpdateHandler(
    handleMessageWithLowBattery: HandleMessageWithLowBattery
) {
    
    private val log = KotlinLogging.logger {}
    
    fun handle(rq: TgRequest) {
        log.info { rq.update }
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
