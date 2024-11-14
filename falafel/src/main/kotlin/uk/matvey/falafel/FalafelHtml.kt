package uk.matvey.falafel

import kotlinx.html.HtmlBlockTag
import kotlinx.html.id
import kotlinx.html.span
import uk.matvey.falafel.balance.AccountBalance

object FalafelHtml {

    fun HtmlBlockTag.menu(account: AccountBalance?, activeTab: String) {
        menuTab("account", account?.name ?: "Login", activeTab == "account")
        menuTab("clubs", "Clubs", activeTab == "clubs")
        menuTab("titles", "🎞️ Titles", activeTab == "titles")
        menuTab("tmdb", "🗄️ TMDb", activeTab == "tmdb")
        menuTab("tags", "🏷️ Tags", activeTab == "tags")
    }

    fun HtmlBlockTag.menuTab(name: String, label: String, active: Boolean) = span {
        id = "menu-tab-$name"
        attributes["hx-swap-oob"] = "true"
        if (active) {
            attributes["style"] = "font-weight: bold"
        }
        +label
    }
}
