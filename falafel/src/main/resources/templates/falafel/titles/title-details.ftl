<#import "../components/menu.ftl" as menu>

<div class="row gap-16"
     hx-ext="sse"
     sse-connect="/falafel/titles/${title.id}/events"
     sse-swap="message"
>
    <#if title.refs.tmdbPosterPath?has_content>
        <img src="https://image.tmdb.org/t/p/w440_and_h660_face${title.refs.tmdbPosterPath}"
             alt="Poster"
             height="256"
        />
    </#if>
    <div class="col gap-16">
        <div class="t1">${title.title}</div>
        <#if title.releaseYear?has_content>
            <div class="t3">Year: ${title.releaseYear}</div>
        </#if>
        <#if title.directorName?has_content>
            <div class="t3">Director: ${title.directorName}</div>
        </#if>
        <div>
            <#list tags as tag>
                <button hx-post="/falafel/tags/${tag.name}?titleId=${title.id}"
                        hx-swap="none"
                        <#if account.currentBalance lte 0>disabled</#if>
                >
                    ${tag.emoji} <#if tag.count gt 0>${tag.count}</#if>
                </button>
            </#list>
        </div>
    </div>
</div>

<@menu.menuTitles account=account activeTab="titles"/>
