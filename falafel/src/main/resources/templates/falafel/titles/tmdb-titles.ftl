<div class="t3">TMDb titles</div>
<#list titles as title>
    <div class="row gap-16" hx-get="/falafel/titles?tmdbId=${title.id}" hx-target="#content">
        <div class="t3">${title.title}</div>
        <#if title.releaseDate?has_content>
            <div>(${title.releaseDate[0..3]})</div>
        </#if>
        <div>${title.id?c}</div>
        <form hx-post="/falafel/titles" hx-swap="delete">
            <label><input hidden name="tmdbId" value="${title.id?c}"/></label>
            <button type="submit">Save</button>
        </form>
    </div>
</#list>
