package uk.matvey.falafel

import io.ktor.server.auth.AuthenticationContext
import io.ktor.server.auth.AuthenticationProvider
import io.ktor.server.auth.Principal
import uk.matvey.app.AccountPrincipal
import uk.matvey.falafel.balance.BalanceSql.ensureBalance
import uk.matvey.slon.repo.Repo
import java.util.UUID

class FalafelAuth(
    private val repo: Repo,
) : AuthenticationProvider(object : Config("falafel") {}) {

    class FalafelPrincipal(val accountId: UUID, val name: String, val balanceQuantity: Int) : Principal

    override suspend fun onAuthenticate(context: AuthenticationContext) {
        context.principal<AccountPrincipal>()?.let { accountPrincipal ->
            val accountId = accountPrincipal.id
            val balance = repo.access { a -> a.ensureBalance(accountId) }
            context.principal(FalafelPrincipal(accountId, accountPrincipal.name, 100))
        }
    }
}
