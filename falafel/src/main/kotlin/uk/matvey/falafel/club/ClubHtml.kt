package uk.matvey.falafel.club

import kotlinx.html.HtmlBlockTag
import kotlinx.html.button
import kotlinx.html.div
import uk.matvey.falafel.FalafelHtml.menu
import uk.matvey.falafel.balance.AccountBalance
import java.util.UUID

object ClubHtml {

    fun HtmlBlockTag.clubsPage(clubs: List<Club>, account: AccountBalance, clubId: UUID?) {
        div(classes = "col gap-16") {
            div(classes = "t1") {
                +"Clubs"
            }
            div {
                +"Current club: $clubId"
            }
            if (clubId != null) {
                button {
                    attributes["hx-get"] = "/falafel/clubs"
                    attributes["hx-target"] = "#content"
                    attributes["hx-push-url"] = "true"
                    +"Enter personal profile"
                }
            }
            clubs.forEach { club ->
                div(classes = "row gap-8") {
                    div(classes = "t3") {
                        +club.name
                    }
                    if (club.id != clubId) {
                        button {
                            attributes["hx-get"] = "/falafel/clubs/${club.id}/clubs"
                            attributes["hx-target"] = "#content"
                            attributes["hx-push-url"] = "true"
                            +"Enter"
                        }
                    }
                }
            }
            button {
                attributes["hx-get"] = path("new-club-form", clubId)
                attributes["hx-target"] = "#content"
                +"New club"
            }
        }
        menu(account, "clubs")
    }

    fun path(path: String, clubId: UUID?) = clubId?.let {
        "/falafel/clubs/$clubId/clubs/$path"
    } ?: "/falafel/clubs/$path"
}