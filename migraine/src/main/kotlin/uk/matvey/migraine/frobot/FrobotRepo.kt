package uk.matvey.migraine.frobot

import kotlinx.serialization.encodeToString
import uk.matvey.dukt.json.JsonSetup.JSON
import uk.matvey.migraine.frobot.FrobotSql.CREATED_AT
import uk.matvey.migraine.frobot.FrobotSql.FROBOT
import uk.matvey.migraine.frobot.FrobotSql.ID
import uk.matvey.migraine.frobot.FrobotSql.STATE
import uk.matvey.migraine.frobot.FrobotSql.TG
import uk.matvey.migraine.frobot.FrobotSql.UPDATED_AT
import uk.matvey.postal.QueryParam.JsonbParam
import uk.matvey.postal.QueryParam.TextParam
import uk.matvey.postal.QueryParam.TimestampParam
import uk.matvey.postal.QueryParam.UuidParam
import uk.matvey.postal.QueryParams
import uk.matvey.postal.Repo
import uk.matvey.postal.ResultExtractor
import java.time.Instant
import java.util.UUID

class FrobotRepo(
    private val repo: Repo,
) {
    
    fun add(frobot: Frobot) {
        repo.insert(
            FROBOT, QueryParams()
                .add(ID, UuidParam(frobot.id))
                .add(STATE, TextParam(frobot.state.name))
                .add(TG, JsonbParam(JSON.encodeToString(frobot.tg)))
                .add(CREATED_AT, TimestampParam(frobot.createdAt))
                .add(UPDATED_AT, TimestampParam(frobot.updatedAt))
        )
    }
    
    fun update(frobot: Frobot) {
        repo.update(
            FROBOT, QueryParams()
                .add(STATE, TextParam(frobot.state.name))
                .add(TG, JsonbParam(JSON.encodeToString(frobot.tg)))
                .add(UPDATED_AT, TimestampParam(Instant.now())),
            "$ID = ? and $UPDATED_AT = ?",
            QueryParams().add(ID, UuidParam(frobot.id)).add(UPDATED_AT, TimestampParam(frobot.updatedAt))
        )
    }
    
    fun get(id: UUID): Frobot {
        return repo.select(
            "select * from $FROBOT where $ID = ?",
            QueryParams().add(ID, UuidParam(id)),
            ::frobotFrom
        )
            .single()
    }
    
    fun findByTgUserId(userId: Long): Frobot? {
        return repo.select(
            "select * from $FROBOT where $TG ->> 'userId' = ?",
            QueryParams().add("tgUserId", TextParam(userId.toString())),
            ::frobotFrom
        )
            .singleOrNull()
    }
    
    private fun frobotFrom(ex: ResultExtractor): Frobot {
        return Frobot(
            id = ex.uuid(ID),
            tg = JSON.decodeFromString(ex.jsonb(TG)),
            state = Frobot.State.valueOf(ex.string(STATE)),
            createdAt = ex.instant(CREATED_AT),
            updatedAt = ex.instant(UPDATED_AT),
        )
    }
}
