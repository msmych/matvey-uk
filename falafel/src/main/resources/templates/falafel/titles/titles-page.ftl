<#import "../components/menu.ftl" as menu>

<div class="col gap-16">
    <div class="t1">Titles</div>
    <form class="row gap-16" hx-get="/falafel/titles/search" hx-target="#titles-list">
        <label>
            <input name="q" type="text" required placeholder="pulp fiction">
        </label>
        <button class="primary" type="submit">Search</button>
    </form>
    <div id="titles-list"></div>
    <div>
        If you couldn't find a title your were looking for, try searching with TMDb:
    </div>
    <button hx-get="/falafel/tmdb" hx-target="#content">Search with TMDb</button>
</div>

<!-- OOB -->
<@menu.menu account=account oob=true/>
