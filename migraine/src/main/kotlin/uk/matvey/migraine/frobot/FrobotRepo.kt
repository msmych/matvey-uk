package uk.matvey.migraine.frobot

import kotlinx.serialization.encodeToString
import uk.matvey.dukt.json.JsonSetup.JSON
import uk.matvey.migraine.frobot.FrobotSql.CREATED_AT
import uk.matvey.migraine.frobot.FrobotSql.FROBOT
import uk.matvey.migraine.frobot.FrobotSql.ID
import uk.matvey.migraine.frobot.FrobotSql.STATE
import uk.matvey.migraine.frobot.FrobotSql.TG
import uk.matvey.migraine.frobot.FrobotSql.UPDATED_AT
import uk.matvey.slon.QueryParam.Companion.jsonb
import uk.matvey.slon.QueryParam.Companion.text
import uk.matvey.slon.QueryParam.Companion.timestamp
import uk.matvey.slon.QueryParam.Companion.uuid
import uk.matvey.slon.Repo
import uk.matvey.slon.command.Insert.Builder.Companion.insert
import uk.matvey.slon.command.Update.Builder.Companion.update
import uk.matvey.slon.query.RecordReader
import java.time.Instant
import java.util.UUID

class FrobotRepo(
    private val repo: Repo,
) {

    fun add(frobot: Frobot) {
        repo.execute(
            insert(FROBOT)
                .values(
                    ID to uuid(frobot.id),
                    STATE to text(frobot.state.name),
                    TG to jsonb(JSON.encodeToString(frobot.tg)),
                    CREATED_AT to timestamp(frobot.createdAt),
                    UPDATED_AT to timestamp(frobot.updatedAt),
                )
        )
    }

    fun update(frobot: Frobot) {
        repo.execute(
            update(FROBOT)
                .set(
                    STATE to text(frobot.state.name),
                    TG to jsonb(JSON.encodeToString(frobot.tg)),
                    UPDATED_AT to timestamp(Instant.now())
                )
                .where(
                    "$ID = ? and $UPDATED_AT = ?",
                    uuid(frobot.id),
                    timestamp(frobot.updatedAt)
                )
        )
    }

    fun get(id: UUID): Frobot {
        return repo.query(
            "SELECT * FROM $FROBOT WHERE $ID = ?",
            listOf(uuid(id)),
            ::frobotFrom
        )
            .single()
    }

    fun findByTgUserId(userId: Long): Frobot? {
        return repo.query(
            "SELECT * FROM $FROBOT WHERE $TG ->> 'userId' = ?",
            listOf(text(userId.toString())),
            ::frobotFrom
        )
            .singleOrNull()
    }

    private fun frobotFrom(reader: RecordReader): Frobot {
        return Frobot(
            id = reader.uuid(ID),
            tg = JSON.decodeFromString(reader.string(TG)),
            state = Frobot.State.valueOf(reader.string(STATE)),
            createdAt = reader.instant(CREATED_AT),
            updatedAt = reader.instant(UPDATED_AT),
        )
    }
}
