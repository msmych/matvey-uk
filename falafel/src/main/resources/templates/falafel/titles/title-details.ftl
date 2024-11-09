<#import "../components/menu.ftl" as menu>

<div class="col gap-16"
     hx-ext="sse"
     sse-connect="/falafel/titles/${title.id}/events"
     sse-swap="message"
>
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

<@menu.menuTitles account=account activeTab="titles"/>
