<#list titles as title>
    <div class="col gap-8">
        <div class="t3">${title.title}</div>
        <div id="tag-${title.id}"
             class="row gap-8"
             hx-get="/falafel/titles/${title.id}/tags"
             hx-trigger="load, every 1m">
        </div>
    </div>
</#list>
