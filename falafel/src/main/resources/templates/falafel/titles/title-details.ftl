<div class="col gap-16">
    <div class="t1">${title.title}</div>
    <#if title.releaseYear?has_content>
        <div class="t3">Year: ${title.releaseYear}</div>
    </#if>
    <#if title.directorName?has_content>
        <div class="t3">Director: ${title.directorName}</div>
    </#if>
    <div id="tags-${title.id}"
         hx-get="/falafel/titles/${title.id}/tags-edit"
         hx-trigger="load, every 1m"
    ></div>
    <div class="col gap-8"
         hx-ext="sse"
         sse-connect="/falafel/titles/${title.id}/events"
         sse-swap="message"
         hx-swap="beforeend"
    ></div>
</div>
