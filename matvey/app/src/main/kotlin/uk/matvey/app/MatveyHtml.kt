package uk.matvey.app

import kotlinx.html.BODY
import kotlinx.html.HTML
import kotlinx.html.a
import kotlinx.html.body
import kotlinx.html.div
import kotlinx.html.head
import kotlinx.html.id
import kotlinx.html.lang
import kotlinx.html.link
import kotlinx.html.meta
import kotlinx.html.style
import kotlinx.html.title
import kotlinx.html.unsafe
import uk.matvey.app.account.AccountPrincipal
import uk.matvey.utka.ktor.htmx.HtmxKit.htmxScript
import uk.matvey.utka.ktor.htmx.HtmxKit.hxGet

object MatveyHtml {

    fun HTML.index(account: AccountPrincipal?, assetsUrl: String) {
        head(assetsUrl)
        body {
            div(classes = "col gap-16") {
                id = "home"
                div(classes = "row split") {
                    id = "menu"
                    div(classes = "row gap-8") {
                        a(classes = "tab click active") {
                            href = "/"
                            +"Home"
                        }
                        account?.let {
                            a(classes = "tab click") {
                                href = "/falafel"
                                +"Falafel"
                            }
                        }
                    }
                    div(classes = "row gap-8") {
                        if (account != null) {
                            div(classes = "tab click") {
                                id = "account-tab"
                                hxGet(path = "/me", target = "#content")
                                +"👤 ${account.name}"
                            }
                        } else {
                            a(classes = "tab click") {
                                href = "/login"
                                +"Login"
                            }
                        }
                    }
                }
            }
            style()
        }
    }

    private fun HTML.head(assetsUrl: String) = head {
        lang = "en"
        title = "Matvey"
        meta {
            name = "viewport"
            content = "width=device-width, initial-scale=1"
        }
        link {
            rel = "stylesheet"
            href = "/assets/style.css"
        }
        link {
            rel = "apple-touch-icon"
            sizes = "180x180"
            href = "$assetsUrl/favicons/apple-touch-icon.png"
        }
        link {
            rel = "icon"
            type = "image/png"
            sizes = "32x32"
            href = "$assetsUrl/favicons/favicon-32x32.png"
        }
        link {
            rel = "icon"
            type = "image/png"
            sizes = "16x16"
            href = "$assetsUrl/favicons/favicon-16x16.png"
        }
        link {
            rel = "manifest"
            href = "$assetsUrl/favicons/site.webmanifest"
        }
        htmxScript()
    }

    private fun BODY.style() = style {
        unsafe {
            +"""
#home {
    margin: 64px auto 0;
    width: 80%;
    min-width: 320px;
    max-width: 2048px;
}

.tab {
    padding: 8px 16px;
    border-radius: 8px;
}

button {
    padding: 8px 16px;
    border-radius: 8px;
    font-size: 1em;
    max-width: 256px;
    cursor: pointer;
    background: transparent;
    border: none;
}

button.primary {
    color: white;
    background-color: darkcyan;
}

input {
    padding: 8px 16px;
    border: lightgray 2px solid;
    border-radius: 8px;
    font-size: 1em;
}
                """.trimIndent()
        }
    }
}
