<#import "../components/menu.ftl" as menu>

<div class="col gap-16">
    <div class="t1">Titles</div>
    <form class="row gap-16" hx-get="/falafel/titles/search" hx-target="#titles-list">
        <label>
            <input name="q" type="text" required placeholder="falafel">
        </label>
        <button class="primary" type="submit">Search</button>
        <div class="htmx-indicator">🍿</div>
    </form>
    <div id="titles-list" class="col gap-32" hx-get="/falafel/titles/search?q=" hx-trigger="load"></div>
    <div>
        If you couldn't find a title your were looking for, try searching with TMDb:
    </div>
    <button hx-get="/falafel/tmdb" hx-target="#content">Search with TMDb</button>
    <div class="col gap-8"
         hx-ext="sse"
         sse-connect="/falafel/titles/events"
         sse-swap="message"
         hx-swap="beforeend"
    ></div>
</div>

<!-- OOB -->
<@menu.menu account=account oob=true/>
