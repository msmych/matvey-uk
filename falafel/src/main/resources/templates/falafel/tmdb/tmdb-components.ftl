<#macro movie movie>
    <div id="tmdb-${movie.id}" class="row gap-16">
        <#if movie.posterPath?has_content>
            <img src="https://image.tmdb.org/t/p/w440_and_h660_face${movie.posterPath}"
                 alt="Poster"
                 height="128"
            />
        </#if>
        <div class="col gap-16">
            <div class="t3">${movie.title}</div>
            <#if movie.releaseYear?has_content>
                <div class="t3">(${movie.releaseYear})</div>
            </#if>
            <#if movie.titleId?has_content>
                <div class="row gap-16">
                    <div>☑️</div>
                    <button hx-get="/falafel/titles/${movie.titleId}"
                            hx-target="#content"
                            hx-push-url="true"
                    >
                        View
                    </button>
                </div>
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
    </div>
</#macro>
