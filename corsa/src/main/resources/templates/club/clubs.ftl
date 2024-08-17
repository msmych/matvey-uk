<h2>Clubs</h2>
<div class="col gap16">
    <#list clubs as club>
        <#include "club.ftl">
    </#list>
    <div id="new-club">
        <button hx-get="/clubs/new-club-form" hx-target="#new-club" hx-swap="innerHTML">Add club</button>
    </div>
</div>