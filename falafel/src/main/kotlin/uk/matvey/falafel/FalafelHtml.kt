package uk.matvey.falafel

import kotlinx.html.HtmlBlockTag
import kotlinx.html.a
import kotlinx.html.button
import kotlinx.html.div
import kotlinx.html.id
import kotlinx.html.span
import uk.matvey.falafel.balance.AccountBalance
import uk.matvey.falafel.club.Club

object FalafelHtml {

    fun HtmlBlockTag.menu(activeTab: String, account: AccountBalance?, currentClub: Club?) =
        div(classes = "menu col gap-16") {
            id = "menu"
            attributes["hx-swap-oob"] = "true"
            if (account != null) {
                button {
                    attributes["hx-get"] = "/falafel/me"
                    attributes["hx-target"] = "#content"
                    attributes["hx-push-url"] = "true"
                    span {
                        id = "menu-tab-account"
                        if (activeTab == "account") {
                            attributes["style"] = "font-weight: bold"
                        }
                        +account.name
                    }
                }
            } else {
                a(classes = "click") {
                    href = "/login"
                    +"Login"
                }
            }
            button {
                attributes["hx-get"] = "/falafel/"
                attributes["hx-target"] = "#content"
                attributes["hx-push-url"] = "true"
                span {
                    id = "menu-tab-${"clubs"}"
                    if (activeTab == "clubs") {
                        attributes["style"] = "font-weight: bold"
                    }
                    +(currentClub?.name ?: "👤 Personal")
                }
            }
            button {
                attributes["hx-get"] = "/falafel/"
                attributes["hx-target"] = "#content"
                attributes["hx-push-url"] = "true"
                span {
                    id = "menu-tab-${"titles"}"
                    if (activeTab == "titles") {
                        attributes["style"] = "font-weight: bold"
                    }
                    +"🎞️ Titles"
                }
            }
            button {
                attributes["hx-get"] = "/falafel/"
                attributes["hx-target"] = "#content"
                attributes["hx-push-url"] = "true"
                span {
                    id = "menu-tab-${"tmdb"}"
                    if (activeTab == "tmdb") {
                        attributes["style"] = "font-weight: bold"
                    }
                    +"🗄️ TMDb"
                }
            }
            button {
                attributes["hx-get"] = "/falafel/"
                attributes["hx-target"] = "#content"
                attributes["hx-push-url"] = "true"
                span {
                    id = "menu-tab-${"tags"}"
                    if (activeTab == "tags") {
                        attributes["style"] = "font-weight: bold"
                    }
                    +"🏷️ Tags"
                }
            }
        }

}
