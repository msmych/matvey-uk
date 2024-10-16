<#import "tmdb-components.ftl" as tmdb/>

<#list movies as movie>
    <div class="row gap-16">
        <div class="t3">${movie.title}</div>
        <#if movie.releaseYear?has_content>
            <div>(${movie.releaseYear})</div>
        </#if>
        <#if movie.titleId?has_content>
            <@tmdb.movieSaved titleId=movie.titleId/>
        <#else>
            <form class="row gap-16" hx-post="/falafel/tmdb" hx-swap="outerHTML">
                <label>
                    <input hidden name="tmdbId" value="${movie.id}"/>
                </label>
                <button type="submit">Save</button>
                <div class="htmx-indicator">🍿</div>
            </form>
        </#if>
    </div>
</#list>
