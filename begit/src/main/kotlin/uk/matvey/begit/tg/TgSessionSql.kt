package uk.matvey.begit.tg

import kotlinx.serialization.encodeToString
import uk.matvey.kit.json.JsonKit.JSON
import uk.matvey.slon.Access
import uk.matvey.slon.InsertBuilder.Companion.insertInto
import uk.matvey.slon.RecordReader
import uk.matvey.slon.param.IntParam.Companion.int
import uk.matvey.slon.param.JsonbParam.Companion.jsonb
import uk.matvey.slon.param.PlainParam.Companion.now
import uk.matvey.slon.param.TimestampParam.Companion.timestamp
import uk.matvey.slon.query.update.UpdateQuery.Builder.Companion.update

object TgSessionSql {

    const val TG_SESSIONS = "begit.tg_sessions"

    const val CHAT_ID = "chat_id"
    const val DATA = "data"
    const val CREATED_AT = "created_at"
    const val UPDATED_AT = "updated_at"

    fun Access.ensureTgSession(chatId: Long): TgSession {
        return execute(
            insertInto(TG_SESSIONS)
                .set(
                    CHAT_ID to int(chatId),
                    CREATED_AT to now(),
                    UPDATED_AT to now(),
                )
                .onConflictDoNothing()
                .returningOne { r -> r.readTgSession() }
        )
    }

    fun Access.getTgSessionByChatId(chatId: Long): TgSession {
        return requireNotNull(findTgSessionByChatId(chatId))
    }

    fun Access.findTgSessionByChatId(chatId: Long): TgSession? {
        return queryOneNullable("select * from $TG_SESSIONS where $CHAT_ID = ?", listOf(int(chatId))) { r -> r.readTgSession() }
    }

    fun Access.updateTgSession(tgSession: TgSession) {
        execute(
            update(TG_SESSIONS)
                .set(DATA to jsonb(JSON.encodeToString(tgSession.data)), UPDATED_AT to now())
                .where("$CHAT_ID = ? and $UPDATED_AT = ?", int(tgSession.chatId), timestamp(tgSession.updatedAt))
                .optimistic()
        )
    }

    fun RecordReader.readTgSession(): TgSession {
        return TgSession(
            chatId = long(CHAT_ID),
            data = JSON.decodeFromString(string(DATA)),
            createdAt = instant(CREATED_AT),
            updatedAt = instant(UPDATED_AT),
        )
    }
}