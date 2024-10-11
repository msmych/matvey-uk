package uk.matvey.falafel.balance

import uk.matvey.app.account.Account.Tag
import uk.matvey.app.account.AccountPrincipal
import java.util.UUID

class AccountBalance(
    val accountId: UUID,
    val name: String,
    val tags: Set<Tag>,
    val currentBalance: Int,
) {

    fun isAdmin() = Tag.ADMIN in tags

    companion object {
        fun from(account: AccountPrincipal, balance: Balance): AccountBalance {
            return AccountBalance(
                accountId = account.id,
                name = account.name,
                tags = account.tags,
                currentBalance = balance.current
            )
        }
    }
}
