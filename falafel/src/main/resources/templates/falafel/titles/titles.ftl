<#import "../components/menu.ftl" as menu>

<div class="col gap-16">
    <div class="t1">Titles</div>
    <div class="row gap-16">
        <label>
            <input name="q" type="text" required placeholder="Pulp Fiction">
        </label>
        <button class="primary" hx-get="/falafel/titles/search" hx-include="[name='q']">Search</button>
        <button hx-get="/falafel/titles/search-tmdb" hx-include="[name='q']" hx-target="#tmdb-titles">Search TMDb</button>
    </div>
    <#list titles as title>
        <div class="col gap-8">
            <div class="t3">${title.title}</div>
            <div id="tag-${title.id}"
                 class="row gap-8"
                 hx-get="/falafel/tags?titleId=${title.id}"
                 hx-trigger="load">
            </div>
        </div>
    </#list>
    <div id="tmdb-titles" class="col gap-16"></div>
</div>

<!-- OOB -->
<@menu.menu account=account oob=true/>
