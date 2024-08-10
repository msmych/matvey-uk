package uk.matvey.drinki.account

class AccountService(
    private val accountRepo: AccountRepo,
) {
    
    suspend fun ensureTgAccount(tgUserId: Long): Account {
        val existingAccount = accountRepo.findByTgUserId(tgUserId)
        if (existingAccount != null) {
            return existingAccount
        }
        accountRepo.add(Account.tgAccount(tgUserId))
        return accountRepo.getByTgUserId(tgUserId)
    }
}
