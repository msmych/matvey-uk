package uk.matvey.falafel.title

import uk.matvey.falafel.FalafelSql.FALAFEL
import uk.matvey.kit.json.JsonKit.jsonDeserialize
import uk.matvey.slon.RecordReader

object TitleSql {

    const val TITLES = "$FALAFEL.titles"

    fun readTitle(reader: RecordReader): Title {
        return Title(
            id = reader.uuid("id"),
            state = Title.State.valueOf(reader.string("state")),
            title = reader.string("title"),
            clubId = reader.uuidOrNull("club_id"),
            refs = jsonDeserialize(reader.string("refs")),
            createdBy = reader.uuidOrNull("created_by"),
            createdAt = reader.instant("created_at"),
            updatedAt = reader.instant("updated_at"),
        )
    }
}
