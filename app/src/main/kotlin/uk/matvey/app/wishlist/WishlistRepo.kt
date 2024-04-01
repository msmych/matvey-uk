package uk.matvey.app.wishlist

import uk.matvey.app.wishlist.WishlistItem.Priority
import uk.matvey.app.wishlist.WishlistItem.State
import uk.matvey.app.wishlist.WishlistSql.CREATED_AT
import uk.matvey.app.wishlist.WishlistSql.DESCRIPTION
import uk.matvey.app.wishlist.WishlistSql.ID
import uk.matvey.app.wishlist.WishlistSql.NAME
import uk.matvey.app.wishlist.WishlistSql.PRIORITY
import uk.matvey.app.wishlist.WishlistSql.STATE
import uk.matvey.app.wishlist.WishlistSql.UPDATED_AT
import uk.matvey.app.wishlist.WishlistSql.URL
import uk.matvey.app.wishlist.WishlistSql.WISHLIST
import uk.matvey.postal.QueryParam
import uk.matvey.postal.QueryParam.TextParam
import uk.matvey.postal.QueryParam.TimestampParam
import uk.matvey.postal.QueryParams
import uk.matvey.postal.Repo
import java.net.URI

class WishlistRepo(private val repo: Repo) {
    
    fun add(wishlistItem: WishlistItem) {
        repo.insert(
            WISHLIST,
            QueryParams()
                .add(ID, QueryParam.UuidParam(wishlistItem.id))
                .add(NAME, TextParam(wishlistItem.name))
                .add(STATE, TextParam(wishlistItem.state.name))
                .add(PRIORITY, TextParam(wishlistItem.priority.name))
                .add(DESCRIPTION, TextParam(wishlistItem.description))
                .add(URL, TextParam(wishlistItem.url?.toString()))
                .add(CREATED_AT, TimestampParam(wishlistItem.createdAt))
                .add(UPDATED_AT, TimestampParam(wishlistItem.updatedAt))
        )
    }
    
    fun findAllWanted(): List<WishlistItem> {
        return repo.select(
            "select * from $WISHLIST where $STATE = 'WANTED' order by $CREATED_AT desc",
            QueryParams(),
        ) { ex ->
            WishlistItem(
                id = ex.uuid(ID),
                name = ex.string(NAME),
                state = State.valueOf(ex.string(STATE)),
                priority = Priority.valueOf(ex.string(PRIORITY)),
                description = ex.stringOrNull(DESCRIPTION),
                url = ex.stringOrNull(URL)?.let(::URI),
                createdAt = ex.instant(CREATED_AT),
                updatedAt = ex.instant(UPDATED_AT),
            )
        }
    }
}
