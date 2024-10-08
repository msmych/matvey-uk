package uk.matvey.migraine.frobot

import kotlinx.serialization.encodeToString
import uk.matvey.kit.json.JsonKit.JSON
import uk.matvey.kit.time.TimeKit.instant
import uk.matvey.migraine.frobot.FrobotSql.CREATED_AT
import uk.matvey.migraine.frobot.FrobotSql.FROBOT
import uk.matvey.migraine.frobot.FrobotSql.ID
import uk.matvey.migraine.frobot.FrobotSql.STATE
import uk.matvey.migraine.frobot.FrobotSql.TG
import uk.matvey.migraine.frobot.FrobotSql.UPDATED_AT
import uk.matvey.slon.RecordReader
import uk.matvey.slon.repo.Repo
import uk.matvey.slon.repo.RepoKit.insertOneInto
import uk.matvey.slon.repo.RepoKit.queryOne
import uk.matvey.slon.repo.RepoKit.queryOneOrNull
import uk.matvey.slon.repo.RepoKit.update
import uk.matvey.slon.value.PgJsonb.Companion.toPgJsonb
import uk.matvey.slon.value.PgTimestamp.Companion.toPgTimestamp
import uk.matvey.slon.value.PgUuid.Companion.toPgUuid
import java.util.UUID

class FrobotRepo(
    private val repo: Repo,
) {

    fun add(frobot: Frobot) {
        repo.insertOneInto(FROBOT) {
            set(ID, frobot.id)
            set(STATE, frobot.state)
            set(TG, JSON.encodeToString(frobot.tg))
            set(CREATED_AT, frobot.createdAt)
            set(UPDATED_AT, frobot.updatedAt)
        }
    }

    fun update(frobot: Frobot) {
        repo.update(FROBOT) {
            set(STATE, frobot.state)
            set(TG, JSON.encodeToString(frobot.tg))
            set(UPDATED_AT, instant())
            where(
                "$ID = ? and $UPDATED_AT = ?",
                frobot.id.toPgUuid(),
                frobot.updatedAt.toPgTimestamp()
            )
        }
    }

    fun get(id: UUID): Frobot {
        return repo.queryOne(
            "select * from $FROBOT where $ID = ?",
            listOf(id.toPgUuid()),
            ::frobotFrom
        )
    }

    fun findByTgUserId(userId: Long): Frobot? {
        return repo.queryOneOrNull(
            "select * from $FROBOT where $TG ->> 'userId' = ?",
            listOf(userId.toString().toPgJsonb()),
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
