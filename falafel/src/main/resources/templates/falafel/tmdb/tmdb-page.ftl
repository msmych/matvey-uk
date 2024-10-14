<#import "../components/menu.ftl" as menu/>

<div class="col gap-16">
    <div class="t1">TMDb movies</div>
    <form class="row gap-16"
          hx-get="/falafel/tmdb/search"
          hx-target="#tmdb-movies"
    >
        <label>
            <input name="q" type="text" required placeholder="falafel">
        </label>
        <button class="primary" type="submit">Search</button>
        <div class="htmx-indicator">🍿</div>
    </form>
    <div id="tmdb-movies" class="col gap-16"></div>
</div>

<@menu.menu account=account activeTab="tmdb" oob=true />
