<#list movies as movie>
    <div class="row gap-16">
        <div class="t3">${movie.title}</div>
        <#if movie.releaseYear?has_content>
            <div>(${movie.releaseYear})</div>
        </#if>
        <#if movie.saved>
            <div>☑️</div>
        <#else>
            <form class="row gap-16" hx-post="/falafel/tmdb" hx-swap="delete">
                <label>
                    <input hidden name="tmdbId" value="${movie.id}"/>
                </label>
                <button type="submit">Save</button>
                <div class="htmx-indicator">🍿</div>
            </form>
        </#if>
    </div>
</#list>
