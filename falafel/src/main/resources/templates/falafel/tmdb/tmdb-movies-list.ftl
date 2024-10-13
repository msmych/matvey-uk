<#list movies as movie>
    <div class="row gap-16">
        <div class="t3">${movie.title}</div>
        <#if movie.releaseDate?has_content>
            <div>(${movie.releaseDate[0..3]})</div>
        </#if>
        <form class="row gap-16" hx-post="/falafel/tmdb" hx-swap="delete">
            <label><input hidden name="tmdbId" value="${movie.id?c}"/></label>
            <button type="submit">Save</button>
            <div id="tmdb-search-indicator" class="htmx-indicator">🍿</div>
        </form>
    </div>
</#list>
