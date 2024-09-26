package uk.matvey.falafel

import io.ktor.server.auth.AuthenticationContext
import io.ktor.server.auth.Principal
import uk.matvey.app.AccountPrincipal
import uk.matvey.falafel.balance.BalanceSql.ensureBalance
import uk.matvey.slon.repo.Repo
import java.util.UUID

class FalafelAuth(
    private val repo: Repo,
) {

    class FalafelPrincipal(val accountId: UUID, val name: String, val balanceQuantity: Int) : Principal

    fun onAuthenticate(context: AuthenticationContext) {
        context.principal<AccountPrincipal>()?.let { accountPrincipal ->
            val accountId = accountPrincipal.id
            val balance = repo.access { a -> a.ensureBalance(accountId) }
            context.principal(FalafelPrincipal(accountId, accountPrincipal.name, balance.current))
        }
    }
}
