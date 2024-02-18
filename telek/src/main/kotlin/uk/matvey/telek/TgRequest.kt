package uk.matvey.telek

import com.pengrad.telegrambot.model.Update

class TgRequest(
    val update: Update,
) {
    
    fun userId(): Long {
        return (update.message()?.from() ?: update.callbackQuery().from()).id()
    }
    
    fun messageText(): String? {
        return update.message()?.text()
    }
    
    fun messageId(): Int {
        return (update.message() ?: update.callbackQuery().message()).messageId()
    }
    
    fun isCallbackQuery() = update.callbackQuery() != null
    
    fun callbackQueryId(): String {
        return update.callbackQuery().id()
    }
    
    fun command(): Pair<String?, List<String>> {
        return update.message()?.text()
            ?.takeIf { it.startsWith('/') }
            ?.let { text ->
                val parts = text.split(' ')
                val command = parts[0]
                    .substringAfter('/')
                    .substringBefore('@')
                command to parts.subList(1, parts.size)
            } ?: (null to listOf())
    }
    
    fun callbackQueryCommand(): Pair<String?, List<String>> {
        return update.callbackQuery()?.data()
            ?.takeIf { it.startsWith('/') }
            ?.let { text ->
                val parts = text.split(' ')
                val command = parts[0]
                    .substringAfter('/')
                    .substringBefore('@')
                command to parts.subList(1, parts.size)
            } ?: (null to listOf())
    }
}