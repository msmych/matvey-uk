package uk.matvey.falafel

import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.AuthenticationContext
import io.ktor.server.auth.principal
import uk.matvey.app.account.AccountPrincipal
import uk.matvey.falafel.balance.AccountBalance
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

    fun getAccountBalance(call: ApplicationCall): AccountBalance {
        return requireNotNull(call.principal<AccountPrincipal>()).let {
            val balance = repo.access { a -> a.ensureBalance(it.id) }
            AccountBalance.from(it, balance)
        }
    }
}
