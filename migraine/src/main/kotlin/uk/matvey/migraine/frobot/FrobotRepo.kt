package uk.matvey.migraine.frobot

import kotlinx.serialization.encodeToString
import uk.matvey.dukt.json.JsonSetup.JSON
import uk.matvey.postal.QueryParam
import uk.matvey.postal.QueryParam.UuidParam
import uk.matvey.postal.QueryParams
import uk.matvey.postal.Repo
import java.util.UUID

class FrobotRepo(
    private val repo: Repo,
) {
    
    fun update(frobot: Frobot) {
        repo.update(
            "frobot", QueryParams()
                .add("tg", QueryParam.JsonbParam(JSON.encodeToString(frobot.tg))),
            "id = ?", QueryParams().add("id", UuidParam(frobot.id))
        )
    }
    
    fun get(id: UUID): Frobot {
        return repo.select(
            "select * from frobot where id = ?",
            QueryParams().add("id", UuidParam(id))
        ) { ex ->
            Frobot(
                id = ex.uuid("id"),
                tg = JSON.decodeFromString(ex.jsonb("tg")),
                state = Frobot.State.valueOf(ex.string("state")),
            )
        }
            .single()
    }
}
