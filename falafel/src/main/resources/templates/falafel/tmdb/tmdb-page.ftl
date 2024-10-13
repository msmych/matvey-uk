<div class="col gap-16">
    <div class="t1">TMDb movies</div>
    <form class="row gap-16"
          hx-get="/falafel/tmdb/search"
          hx-target="#tmdb-movies"
          hx-indicator="#tmdb-search-indicator"
    >
        <label>
            <input name="q" type="text" required placeholder="pulp fiction">
        </label>
        <button class="primary" type="submit">Search</button>
        <div id="tmdb-search-indicator" class="htmx-indicator">🍿</div>
    </form>
    <div id="tmdb-movies" class="col gap-16"></div>
</div>
