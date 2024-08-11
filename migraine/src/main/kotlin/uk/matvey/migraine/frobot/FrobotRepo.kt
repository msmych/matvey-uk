package uk.matvey.migraine.frobot

import kotlinx.serialization.encodeToString
import uk.matvey.kit.json.JsonKit.JSON
import uk.matvey.migraine.frobot.FrobotSql.CREATED_AT
import uk.matvey.migraine.frobot.FrobotSql.FROBOT
import uk.matvey.migraine.frobot.FrobotSql.ID
import uk.matvey.migraine.frobot.FrobotSql.STATE
import uk.matvey.migraine.frobot.FrobotSql.TG
import uk.matvey.migraine.frobot.FrobotSql.UPDATED_AT
import uk.matvey.slon.RecordReader
import uk.matvey.slon.param.JsonbParam.Companion.jsonb
import uk.matvey.slon.param.TextParam.Companion.text
import uk.matvey.slon.param.TimestampParam.Companion.timestamp
import uk.matvey.slon.param.UuidParam.Companion.uuid
import uk.matvey.slon.repo.Repo
import uk.matvey.slon.repo.RepoKit.insertInto
import uk.matvey.slon.repo.RepoKit.queryOne
import uk.matvey.slon.repo.RepoKit.queryOneNullable
import uk.matvey.slon.repo.RepoKit.update
import java.time.Instant
import java.util.UUID

class FrobotRepo(
    private val repo: Repo,
) {

    suspend fun add(frobot: Frobot) {
        repo.insertInto(FROBOT) {
            set(
                ID to uuid(frobot.id),
                STATE to text(frobot.state.name),
                TG to jsonb(JSON.encodeToString(frobot.tg)),
                CREATED_AT to timestamp(frobot.createdAt),
                UPDATED_AT to timestamp(frobot.updatedAt),
            )
        }
    }

    suspend fun update(frobot: Frobot) {
        repo.update(FROBOT) {
            set(STATE, text(frobot.state.name))
            set(TG, jsonb(JSON.encodeToString(frobot.tg)))
            set(UPDATED_AT, timestamp(Instant.now()))
            where(
                "$ID = ? and $UPDATED_AT = ?",
                uuid(frobot.id),
                timestamp(frobot.updatedAt)
            )
        }
    }

    suspend fun get(id: UUID): Frobot {
        return repo.queryOne(
            "select * from $FROBOT where $ID = ?",
            listOf(uuid(id)),
            ::frobotFrom
        )
    }

    suspend fun findByTgUserId(userId: Long): Frobot? {
        return repo.queryOneNullable(
            "select * from $FROBOT where $TG ->> 'userId' = ?",
            listOf(text(userId.toString())),
            ::frobotFrom
        )
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
