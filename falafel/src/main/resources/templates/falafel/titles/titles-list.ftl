<#list titles as title>
    <div class="row gap-16 click"
         hx-get="/falafel/titles/${title.id}"
         hx-target="#content"
         hx-push-url="true"
    >
        <#if title.refs.tmdbPosterPath?has_content>
            <img src="https://image.tmdb.org/t/p/w440_and_h660_face${title.refs.tmdbPosterPath}"
                 alt="Poster"
                 height="128"
            />
        </#if>
        <div class="col gap-16">
            <div class="t3">${title.title}</div>
            <div id="tags-${title.id}"
                 class="row gap-8"
                 hx-get="/falafel/titles/${title.id}/tags-view"
                 hx-trigger="load, every 10s"
                 hx-target="this"
                 hx-push-url="false"
            >
            </div>
        </div>
    </div>
</#list>
