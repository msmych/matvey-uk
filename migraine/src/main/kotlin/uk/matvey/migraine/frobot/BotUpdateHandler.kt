package uk.matvey.migraine.frobot

import mu.KotlinLogging
import uk.matvey.telek.TgRequest

class BotUpdateHandler() {
    
    private val log = KotlinLogging.logger {}
    
    fun handle(rq: TgRequest) {
        log.info { rq.update }
    }
}
