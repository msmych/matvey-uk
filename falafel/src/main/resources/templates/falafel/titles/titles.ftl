<div class="col gap-16">
    <div class="t1">Titles</div>
    <#list titles as title>
        <div class="col gap-8">
            <div class="t3">🎞️ ${title.title}</div>
            <div id="tag-${title.id}"
                 class="row gap-8"
                 hx-get="/falafel/tags?titleId=${title.id}"
                 hx-trigger="load">
            </div>
        </div>
    </#list>
    <button hx-get="/falafel/titles/new-title-form" hx-target="#content">New title</button>
</div>
