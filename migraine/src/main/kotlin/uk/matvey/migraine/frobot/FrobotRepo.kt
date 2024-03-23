package uk.matvey.migraine.frobot

import kotlinx.serialization.encodeToString
import uk.matvey.dukt.json.JsonSetup.JSON
import uk.matvey.migraine.frobot.FrobotSql.FROBOT
import uk.matvey.postal.QueryParam
import uk.matvey.postal.QueryParam.TextParam
import uk.matvey.postal.QueryParam.UuidParam
import uk.matvey.postal.QueryParams
import uk.matvey.postal.Repo
import uk.matvey.postal.ResultExtractor
import java.util.UUID

class FrobotRepo(
    private val repo: Repo,
) {
    
    fun add(frobot: Frobot) {
        repo.insert(
            FROBOT, QueryParams()
                .add("id", UuidParam(frobot.id))
                .add("state", TextParam(frobot.state.name))
                .add("tg", QueryParam.JsonbParam(JSON.encodeToString(frobot.tg)))
        )
    }
    
    fun update(frobot: Frobot) {
        repo.update(
            FROBOT, QueryParams()
                .add("state", TextParam(frobot.state.name))
                .add("tg", QueryParam.JsonbParam(JSON.encodeToString(frobot.tg))),
            "id = ?", QueryParams().add("id", UuidParam(frobot.id))
        )
    }
    
    fun get(id: UUID): Frobot {
        return repo.select(
            "select * from $FROBOT where id = ?",
            QueryParams().add("id", UuidParam(id)),
            ::frobotFrom
        )
            .single()
    }
    
    fun findByTgUserId(userId: Long): Frobot? {
        return repo.select(
            "select * from $FROBOT where tg ->> 'userId' = ?",
            QueryParams().add("tgUserId", TextParam(userId.toString())),
            ::frobotFrom
        )
            .singleOrNull()
    }
    
    private fun frobotFrom(ex: ResultExtractor): Frobot {
        return Frobot(
            id = ex.uuid("id"),
            tg = JSON.decodeFromString(ex.jsonb("tg")),
            state = Frobot.State.valueOf(ex.string("state")),
        )
    }
}
