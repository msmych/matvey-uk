package uk.matvey.begit.athlete

import uk.matvey.begit.athlete.AthleteSql.ATHLETES
import uk.matvey.begit.athlete.AthleteSql.TG_CHAT_ID
import uk.matvey.begit.athlete.AthleteSql.getAthleteById
import uk.matvey.begit.athlete.AthleteSql.readAthlete
import uk.matvey.slon.param.TextParam.Companion.text
import uk.matvey.slon.repo.Repo
import uk.matvey.slon.repo.RepoKit.queryOne
import java.util.UUID

class AthleteRepo(
    private val repo: Repo,
) {

    suspend fun getById(id: UUID): Athlete {
        return repo.access { a -> a.getAthleteById(id) }
    }

    suspend fun getByTgChatId(tgChatId: Long): Athlete {
        return repo.queryOne(
            "select * from $ATHLETES where $TG_CHAT_ID = ?",
            listOf(text(tgChatId.toString()))
        ) { r -> r.readAthlete() }
    }
}