<h2>Clubs</h2>
<div class="col gap-8">
    <#list clubs as club>
        <#include "club.ftl">
    </#list>
    <div id="new-club">
        <button hx-get="/clubs/new-club-form" hx-target="#new-club" hx-swap="innerHTML">Add club</button>
    </div>
</div>
<div id="clubs" class="box click u" hx-swap-oob="true">Clubs</div>
<div id="events" class="box click" hx-swap-oob="true">Events</div>
