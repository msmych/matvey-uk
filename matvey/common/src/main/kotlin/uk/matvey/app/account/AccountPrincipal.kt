package uk.matvey.app.account

import uk.matvey.app.account.Account.Tag
import uk.matvey.app.account.Account.Tag.ADMIN
import java.util.UUID

class AccountPrincipal(
    val id: UUID,
    val name: String,
    val tags: Set<Tag>
) {

    fun isAdmin() = ADMIN in tags
}
