package uk.matvey.falafel.club

import uk.matvey.app.MatveySql.UPDATED_AT
import uk.matvey.falafel.FalafelSql.FALAFEL
import uk.matvey.slon.RecordReader
import uk.matvey.slon.access.Access
import uk.matvey.slon.access.AccessKit.queryOneOrNull
import uk.matvey.slon.query.InsertOneQueryBuilder.Companion.insertOneInto
import uk.matvey.slon.query.OnConflict.Companion.doNothing
import uk.matvey.slon.query.ReturningQuery.Companion.returning
import uk.matvey.slon.query.UpdateQueryBuilder
import uk.matvey.slon.value.Pg
import uk.matvey.slon.value.PgUuid.Companion.toPgUuid
import java.time.Instant
import java.util.UUID

object ClubTitleSql {

    const val CLUB_TITLES = "$FALAFEL.club_titles"

    const val CLUB_ID = "club_id"
    const val TITLE_ID = "title_id"
    const val WATCHED = "watched"
    const val SAVED = "saved"

    fun Access.ensureClubTitle(clubId: UUID, titleId: UUID): ClubTitle {
        val existing = queryOneOrNull(
            "select * from $CLUB_TITLES where $CLUB_ID = ? and $TITLE_ID = ?",
            listOf(clubId.toPgUuid(), titleId.toPgUuid()),
            ::readClubTitle
        )
        return existing
            ?: query(insertOneInto(CLUB_TITLES) {
                set(CLUB_ID, clubId)
                set(TITLE_ID, titleId)
                set(UPDATED_AT, Instant.now())
                onConflict(doNothing())
            }.returning { readClubTitle(it) }).single()
    }

    fun Access.toggleWatchedTitle(clubId: UUID, titleId: UUID): ClubTitle {
        return query(UpdateQueryBuilder.update(CLUB_TITLES) {
            set(WATCHED, Pg.plain("not $WATCHED"))
            where("$CLUB_ID = ? and $TITLE_ID = ?", clubId.toPgUuid(), titleId.toPgUuid())
        }.returning { readClubTitle(it) }).single()
    }

    fun Access.toggleSavedTitle(clubId: UUID, titleId: UUID): ClubTitle {
        return query(UpdateQueryBuilder.update(CLUB_TITLES) {
            set(SAVED, Pg.plain("not $SAVED"))
            where("$CLUB_ID = ? and $TITLE_ID = ?", clubId.toPgUuid(), titleId.toPgUuid())
        }.returning { readClubTitle(it) }).single()
    }

    fun readClubTitle(reader: RecordReader): ClubTitle {
        return ClubTitle(
            reader.uuid(CLUB_ID),
            reader.uuid(TITLE_ID),
            reader.bool(WATCHED),
            reader.bool(SAVED),
        )
    }
}
