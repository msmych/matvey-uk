package uk.matvey.app.account

import io.ktor.server.auth.Principal
import uk.matvey.app.account.Account.Tag
import uk.matvey.app.account.Account.Tag.ADMIN
import java.util.UUID

class AccountPrincipal(
    val id: UUID,
    val name: String,
    val tags: Set<Tag>
) : Principal {

    fun isAdmin() = ADMIN in tags
}
