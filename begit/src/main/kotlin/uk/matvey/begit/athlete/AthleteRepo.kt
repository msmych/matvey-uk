package uk.matvey.begit.athlete

import uk.matvey.begit.athlete.AthleteSql.ATHLETES
import uk.matvey.begit.athlete.AthleteSql.TG_CHAT_ID
import uk.matvey.begit.athlete.AthleteSql.readAthlete
import uk.matvey.slon.Repo
import uk.matvey.slon.param.TextParam.Companion.text

class AthleteRepo(
    private val repo: Repo,
) {

    fun getByTgChatId(tgChatId: Long): Athlete {
        return repo.queryOne(
            "select * from $ATHLETES where $TG_CHAT_ID = ?",
            listOf(text(tgChatId.toString()))
        ) { r -> r.readAthlete() }
    }
}