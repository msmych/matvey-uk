package uk.matvey.app

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.UpdatesListener.CONFIRMED_UPDATES_ALL
import com.pengrad.telegrambot.model.request.ParseMode.MarkdownV2
import com.pengrad.telegrambot.request.AnswerCallbackQuery
import com.pengrad.telegrambot.request.EditMessageText
import com.pengrad.telegrambot.request.SendMessage
import com.typesafe.config.Config
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import mu.KotlinLogging
import org.flywaydb.core.Flyway
import uk.matvey.app.wishlist.WishlistItem.State
import uk.matvey.app.wishlist.WishlistRepo
import uk.matvey.app.wishlist.WishlistTg
import uk.matvey.slon.DataSourceKit.hikariDataSource
import uk.matvey.slon.repo.Repo
import uk.matvey.telek.TgRequest
import uk.matvey.telek.TgSendMessageSupport.sendMessage
import java.util.UUID
import javax.sql.DataSource

private val log = KotlinLogging.logger("matvey-bot")

@OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
fun startBot(config: Config) {
    val bot = TelegramBot(config.getString("bot.token"))
    val ds = dataSource(config.getConfig("ds"))
    migrate(ds, System.getenv("CLEAN_DB") == "true")
    val repo = Repo(ds)
    val wishlistRepo = WishlistRepo(repo)
    CoroutineScope(newSingleThreadContext("bot")).launch {
        bot.setUpdatesListener { updates ->
            updates.forEach { update ->
                launch {

                    try {
                        val rq = TgRequest(update)

                        val (command, _) = rq.command()

                        if (command == "start") {
                            bot.sendMessage(rq.userId(), "Ciao")
                        }

                        if (command == "wishlist") {
                            val items = wishlistRepo.findAllActive()
                            val text = WishlistTg.wishlistMessageText(items)
                            val markup = WishlistTg.lockableWishlistItemsMarkup(items, rq.userId())
                            val result = bot.execute(
                                SendMessage(rq.userId(), text).replyMarkup(markup).parseMode(MarkdownV2)
                                    .disableWebPagePreview(true)
                            )
                            log.warn { result }
                        } else {
                            val (queryCommand, queryCommandArgs) = rq.callbackQueryCommand()
                            if (queryCommand == "wishlist_item_lock_toggle") {
                                val itemId = UUID.fromString(queryCommandArgs.first())
                                val item = wishlistRepo.findById(itemId)!!
                                if (item.state == State.WANTED || item.state == State.LOCKED && item.tg.lockedBy == rq.userId()) {
                                    wishlistRepo.update(item.toggleLock(rq.userId()))
                                }
                                val items = wishlistRepo.findAllActive()
                                val text = WishlistTg.wishlistMessageText(items)
                                val markup = WishlistTg.lockableWishlistItemsMarkup(items, rq.userId())
                                bot.execute(
                                    EditMessageText(rq.userId(), rq.messageId(), text).replyMarkup(markup)
                                        .parseMode(MarkdownV2)
                                        .disableWebPagePreview(true)
                                )
                            }
                        }

                        if (rq.isCallbackQuery()) {
                            bot.execute(AnswerCallbackQuery(rq.callbackQueryId()))
                        }
                    } catch (e: Exception) {
                        log.error(e) { "Oops" }
                    }
                }
            }
            CONFIRMED_UPDATES_ALL
        }
    }
}

fun migrate(ds: DataSource, clean: Boolean) {
    val flyway = Flyway.configure()
        .dataSource(ds)
        .schemas("matvey")
        .locations("classpath:db/migration/matvey")
        .defaultSchema("matvey")
        .createSchemas(true)
        .cleanDisabled(!clean)
        .load()
    if (clean) {
        flyway.clean()
    }
    flyway
        .migrate()
}

private fun dataSource(config: Config): DataSource {
    return hikariDataSource(
        config.getString("jdbcUrl"),
        config.getString("username"),
        config.getString("password"),
    )
}
