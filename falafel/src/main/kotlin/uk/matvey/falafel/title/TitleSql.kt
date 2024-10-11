package uk.matvey.falafel.title

import uk.matvey.falafel.FalafelSql.FALAFEL
import uk.matvey.falafel.title.Title.State.ACTIVE
import uk.matvey.kit.json.JsonKit.jsonDeserialize
import uk.matvey.slon.RecordReader
import uk.matvey.slon.access.Access
import uk.matvey.slon.access.AccessKit.insertOneInto
import uk.matvey.slon.repo.Repo
import uk.matvey.slon.repo.RepoKit.queryAll
import uk.matvey.slon.value.Pg
import uk.matvey.slon.value.PgText.Companion.toPgText

object TitleSql {

    const val TITLES = "$FALAFEL.titles"

    const val ID = "id"
    const val STATE = "state"
    const val CLUB_ID = "club_id"
    const val REFS = "refs"
    const val TITLE = "title"
    const val CREATED_BY = "created_by"
    const val CREATED_AT = "created_at"
    const val UPDATED_AT = "updated_at"

    fun Access.addTitle(state: Title.State, title: String) {
        insertOneInto(TITLES) {
            set(STATE, state)
            set(TITLE, title)
            set(UPDATED_AT, Pg.now())
        }
    }

    fun Repo.getActiveTitles(): List<Title> {
        return queryAll(
            "select * from $TITLES where $STATE = ?",
            listOf(ACTIVE.name.toPgText()),
            ::readTitle
        )
    }

    fun readTitle(reader: RecordReader): Title {
        return Title(
            id = reader.uuid(ID),
            state = Title.State.valueOf(reader.string(STATE)),
            title = reader.string(TITLE),
            clubId = reader.uuidOrNull(CLUB_ID),
            refs = jsonDeserialize(reader.string(REFS)),
            createdBy = reader.uuidOrNull(CREATED_BY),
            createdAt = reader.instant(CREATED_AT),
            updatedAt = reader.instant(UPDATED_AT),
        )
    }
}
