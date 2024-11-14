package uk.matvey.falafel.title

import kotlinx.html.HTML
import kotlinx.html.HtmlBlockTag
import kotlinx.html.body
import kotlinx.html.button
import kotlinx.html.div
import kotlinx.html.img
import uk.matvey.falafel.FalafelHtml.menu
import uk.matvey.falafel.balance.AccountBalance
import uk.matvey.falafel.club.ClubTitle
import uk.matvey.falafel.tag.TagFtl.TagCount
import java.time.Year
import java.util.UUID

object TitleHtml {

    fun HTML.titleDetailsPage(title: Title, clubTitle: ClubTitle, tags: List<TagCount>, account: AccountBalance) =
        body {
            div(classes = "row gap-16") {
                attributes["hx-ext"] = "sse"
                attributes["sse-connect"] = "/falafel/titles/${title.id}/events"
                attributes["sse-swap"] = "message"
                titleDetails(title, clubTitle, tags, account)
            }
            menu(account, "titles")
        }

    fun HtmlBlockTag.titleDetails(
        title: Title,
        clubTitle: ClubTitle,
        tags: List<TagCount>,
        account: AccountBalance
    ) {
        title.refs.tmdbPosterPath?.let {
            titlePoster(it)
        }
        div(classes = "col gap-16") {
            titleTitle(title.title)
            title.releaseYear?.let {
                titleYear(it)
            }
            title.directorName?.let {
                titleDirector(it)
            }
            div {
                titleWatched(title.id, clubTitle.watched)
                titleSaved(title.id, clubTitle.saved)
            }
            div {
                tags.forEach { (tag, count, emoji) ->
                    button {
                        attributes["hx-post"] = "/falafel/tags/${tag}?titleId=${title.id}"
                        attributes["hx-swap"] = "none"
                        disabled = account.currentBalance <= 0
                        +emoji
                        count.takeIf { it > 0 }?.let {
                            +" $it"
                        }
                    }
                }
            }
        }
    }

    fun HtmlBlockTag.titleTitle(title: String) = div(classes = "t1") {
        +title
    }

    fun HtmlBlockTag.titleYear(year: Year) = div(classes = "t3") {
        +"Year: $year"
    }

    fun HtmlBlockTag.titleDirector(director: String) = div(classes = "t3") {
        +"Director: $director"
    }

    fun HtmlBlockTag.titleWatched(
        titleId: UUID,
        watched: Boolean
    ) {
        button {
            attributes["hx-patch"] = "/falafel/titles/$titleId/watched"
            attributes["hx-swap"] = "outerHTML"
            if (watched) {
                +"Watched"
            } else {
                +"Mark as watched"
            }
        }
    }

    fun HtmlBlockTag.titleSaved(
        titleId: UUID,
        saved: Boolean
    ) {
        button {
            attributes["hx-patch"] = "/falafel/titles/$titleId/saved"
            attributes["hx-swap"] = "outerHTML"
            if (saved) {
                +"Saved"
            } else {
                +"Save"
            }
        }
    }

    fun HtmlBlockTag.titlePoster(posterPath: String) = img {
        src = "https://image.tmdb.org/t/p/w440_and_h660_face$posterPath"
        alt = "Poster"
        height = "256"
    }
}
