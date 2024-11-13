package uk.matvey.falafel

import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.AuthenticationContext
import io.ktor.server.auth.principal
import uk.matvey.app.AuthException
import uk.matvey.app.account.AccountPrincipal
import uk.matvey.falafel.balance.AccountBalance
import uk.matvey.falafel.balance.AccountSql.ensureAccount
import uk.matvey.slon.repo.Repo

class FalafelAuth(
    private val repo: Repo,
) {

    fun onAuthenticate(context: AuthenticationContext) {
        context.principal<AccountPrincipal>()?.let { accountPrincipal ->
            val accountId = accountPrincipal.id
            repo.access { a -> a.ensureAccount(accountId) }
        }
    }

    fun getAccountBalanceOrNull(call: ApplicationCall): AccountBalance? {
        return call.principal<AccountPrincipal>()?.let {
            val balance = repo.access { a -> a.ensureAccount(it.id) }
            return AccountBalance.from(it, balance)
        }
    }

    fun getAccountBalance(call: ApplicationCall): AccountBalance {
        return getAccountBalanceOrNull(call) ?: throw AuthException()
    }
}
