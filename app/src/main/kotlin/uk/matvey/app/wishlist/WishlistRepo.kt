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
import uk.matvey.dukt.json.JsonSetup.JSON
import uk.matvey.postal.QueryParam.JsonbParam
import uk.matvey.postal.QueryParam.TextArrayParam
import uk.matvey.postal.QueryParam.TextParam
import uk.matvey.postal.QueryParam.TimestampParam
import uk.matvey.postal.QueryParam.UuidParam
import uk.matvey.postal.QueryParams
import uk.matvey.postal.Repo
import uk.matvey.postal.ResultExtractor
import java.net.URI
import java.util.UUID

class WishlistRepo(private val repo: Repo) {
    
    fun add(wishlistItem: WishlistItem) {
        repo.insert(
            WISHLIST,
            QueryParams()
                .add(ID, UuidParam(wishlistItem.id))
                .add(NAME, TextParam(wishlistItem.name))
                .add(STATE, TextParam(wishlistItem.state.name))
                .add(TAGS, TextArrayParam(wishlistItem.tags.map { it.toString() }))
                .add(DESCRIPTION, TextParam(wishlistItem.description))
                .add(URL, TextParam(wishlistItem.url?.toString()))
                .add(TG, JsonbParam(JSON.encodeToString(wishlistItem.tg)))
                .add(CREATED_AT, TimestampParam(wishlistItem.createdAt))
                .add(UPDATED_AT, TimestampParam(wishlistItem.updatedAt))
        )
    }
    
    fun update(wishlistItem: WishlistItem) {
        repo.update(
            WISHLIST,
            QueryParams()
                .add(NAME, TextParam(wishlistItem.name))
                .add(STATE, TextParam(wishlistItem.state.name))
                .add(TAGS, TextArrayParam(wishlistItem.tags.map { it.toString() }))
                .add(DESCRIPTION, TextParam(wishlistItem.description))
                .add(URL, TextParam(wishlistItem.url?.toString()))
                .add(TG, JsonbParam(JSON.encodeToString(wishlistItem.tg)))
                .add(UPDATED_AT, TimestampParam(wishlistItem.updatedAt)),
            "$ID = ?",
            QueryParams().add(ID, UuidParam(wishlistItem.id))
        )
    }
    
    fun findById(id: UUID): WishlistItem? {
        return repo.select(
            "select * from $WISHLIST where $ID = ?",
            QueryParams().add(ID, UuidParam(id)),
            ::toWishlistItem
        ).singleOrNull()
    }
    
    fun findAllActive(): List<WishlistItem> {
        return repo.select(
            "select * from $WISHLIST where $STATE in ('WANTED', 'LOCKED') order by $CREATED_AT desc",
            QueryParams(),
            ::toWishlistItem
        )
    }
    
    private fun toWishlistItem(ex: ResultExtractor): WishlistItem {
        return WishlistItem(
            id = ex.uuid(ID),
            name = ex.string(NAME),
            state = State.valueOf(ex.string(STATE)),
            tags = ex.stringList(TAGS).map(WishlistItem.Tag::valueOf).toSet(),
            description = ex.stringOrNull(DESCRIPTION),
            url = ex.stringOrNull(URL)?.let(::URI),
            tg = JSON.decodeFromString(ex.jsonb(TG)),
            createdAt = ex.instant(CREATED_AT),
            updatedAt = ex.instant(UPDATED_AT),
        )
    }
}
