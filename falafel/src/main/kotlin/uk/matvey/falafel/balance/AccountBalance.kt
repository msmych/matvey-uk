package uk.matvey.falafel.balance

import uk.matvey.app.AccountPrincipal
import java.util.UUID

class AccountBalance(
    val accountId: UUID,
    val name: String,
    val currentBalance: Int,
) {

    companion object {
        fun from(account: AccountPrincipal, balance: Balance): AccountBalance {
            return AccountBalance(account.id, account.name, balance.current)
        }
    }
}
