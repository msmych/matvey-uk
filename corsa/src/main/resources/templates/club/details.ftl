<h2>${club.name}</h2>
<h2>Events</h2>
<div class="col gap16">
    <#list events as event>
        <#include "../event/event.ftl">
    </#list>
    <div id="new-event">
        <button hx-get="/events/new-event-form?clubId=${club.id}" hx-target="#new-event" hx-swap="innerHTML">Add event
        </button>
    </div>
</div>