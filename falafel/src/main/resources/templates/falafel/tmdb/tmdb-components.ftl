<#macro movie movie>
    <div id="tmdb-${movie.id}" class="row gap-8">
        <div class="t3">${movie.title}</div>
        <#if movie.releaseYear?has_content>
            <div class="t3">(${movie.releaseYear})</div>
        </#if>
        <#if movie.titleId?has_content>
            <div>☑️</div>
            <button hx-get="/falafel/titles/${movie.titleId}" hx-target="#content">
                View
            </button>
        <#else>
            <form hx-post="/falafel/tmdb" hx-target="#tmdb-${movie.id}" hx-swap="outerHTML">
                <label>
                    <input hidden name="tmdbId" value="${movie.id}"/>
                </label>
                <button type="submit">Save</button>
                <div class="htmx-indicator">🍿</div>
            </form>
        </#if>
    </div>
</#macro>
