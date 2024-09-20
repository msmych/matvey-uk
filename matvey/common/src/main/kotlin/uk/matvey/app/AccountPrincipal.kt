package uk.matvey.app

import io.ktor.server.auth.Principal
import java.util.UUID

class AccountPrincipal(val id: UUID, val name: String) : Principal
