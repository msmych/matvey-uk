package uk.matvey.falafel

import io.ktor.server.auth.AuthenticationContext
import uk.matvey.app.account.AccountPrincipal
import uk.matvey.falafel.balance.BalanceSql.ensureBalance
import uk.matvey.slon.repo.Repo

class FalafelAuth(
    private val repo: Repo,
) {

    fun onAuthenticate(context: AuthenticationContext) {
        context.principal<AccountPrincipal>()?.let { accountPrincipal ->
            val accountId = accountPrincipal.id
            repo.access { a -> a.ensureBalance(accountId) }
        }
    }
}
