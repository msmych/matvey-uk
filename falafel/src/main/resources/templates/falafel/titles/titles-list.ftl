<#list titles as title>
    <div class="col gap-8 click"
         hx-get="/falafel/titles/${title.id}"
         hx-target="#content"
    >
        <div class="t3">${title.title}</div>
        <div id="tags-${title.id}"
             class="row gap-8"
             hx-get="/falafel/titles/${title.id}/tags-view"
             hx-trigger="load, every 1m"
        >
        </div>
    </div>
</#list>
