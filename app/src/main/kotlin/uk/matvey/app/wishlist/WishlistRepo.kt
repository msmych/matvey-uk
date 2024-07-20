package uk.matvey.app.wishlist

import kotlinx.serialization.encodeToString
import uk.matvey.app.wishlist.WishlistItem.State
import uk.matvey.app.wishlist.WishlistSql.CREATED_AT
import uk.matvey.app.wishlist.WishlistSql.DESCRIPTION
import uk.matvey.app.wishlist.WishlistSql.ID
import uk.matvey.app.wishlist.WishlistSql.NAME
import uk.matvey.app.wishlist.WishlistSql.STATE
import uk.matvey.app.wishlist.WishlistSql.TAGS
import uk.matvey.app.wishlist.WishlistSql.TG
import uk.matvey.app.wishlist.WishlistSql.UPDATED_AT
import uk.matvey.app.wishlist.WishlistSql.URL
import uk.matvey.app.wishlist.WishlistSql.WISHLIST
import uk.matvey.kit.json.JsonKit.JSON
import uk.matvey.slon.RecordReader
import uk.matvey.slon.Repo
import uk.matvey.slon.param.ArrayParam.Companion.textArray
import uk.matvey.slon.param.JsonbParam.Companion.jsonb
import uk.matvey.slon.param.TextParam.Companion.text
import uk.matvey.slon.param.TimestampParam.Companion.timestamp
import uk.matvey.slon.param.UuidParam.Companion.uuid
import uk.matvey.slon.query.update.UpdateQuery.Builder.Companion.update
import java.net.URI
import java.util.UUID

class WishlistRepo(private val repo: Repo) {

    fun add(wishlistItem: WishlistItem) {
        repo.insertOne(
            WISHLIST,
            ID to uuid(wishlistItem.id),
            NAME to text(wishlistItem.name),
            STATE to text(wishlistItem.state.name),
            TAGS to textArray(wishlistItem.tags.map { it.toString() }),
            DESCRIPTION to text(wishlistItem.description),
            URL to text(wishlistItem.url?.toString()),
            TG to jsonb(JSON.encodeToString(wishlistItem.tg)),
            CREATED_AT to timestamp(wishlistItem.createdAt),
            UPDATED_AT to timestamp(wishlistItem.updatedAt),
        )
    }

    fun update(wishlistItem: WishlistItem) {
        repo.access { a ->
            a.execute(
                update(WISHLIST)
                    .set(
                        NAME to text(wishlistItem.name),
                        STATE to text(wishlistItem.state.name),
                        TAGS to textArray(wishlistItem.tags.map { it.toString() }),
                        DESCRIPTION to text(wishlistItem.description),
                        URL to text(wishlistItem.url?.toString()),
                        TG to jsonb(JSON.encodeToString(wishlistItem.tg)),
                        UPDATED_AT to timestamp(wishlistItem.updatedAt)
                    )
                    .where("$ID = ?", uuid(wishlistItem.id))
            )
        }
    }

    fun findById(id: UUID): WishlistItem? {
        return repo.queryOneNullable(
            "select * from $WISHLIST where $ID = ?",
            listOf(uuid(id)),
            ::toWishlistItem
        )
    }

    fun findAllActive(): List<WishlistItem> {
        return repo.query(
            "select * from $WISHLIST where $STATE in ('WANTED', 'LOCKED') order by $CREATED_AT desc",
            listOf(),
            ::toWishlistItem
        )
    }

    private fun toWishlistItem(reader: RecordReader): WishlistItem {
        return WishlistItem(
            id = reader.uuid(ID),
            name = reader.string(NAME),
            state = State.valueOf(reader.string(STATE)),
            tags = reader.stringList(TAGS).map(WishlistItem.Tag::valueOf).toSet(),
            description = reader.nullableString(DESCRIPTION),
            url = reader.nullableString(URL)?.let(::URI),
            tg = JSON.decodeFromString(reader.string(TG)),
            createdAt = reader.instant(CREATED_AT),
            updatedAt = reader.instant(UPDATED_AT),
        )
    }
}
