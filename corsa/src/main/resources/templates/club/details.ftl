<div class="col gap-8">
    <#if events?size gt 0>
        <div class="t1">${club.name}</div>
        <div class="col gap-8">
            <#list events as event>
                <#include "../event/event.ftl">
            </#list>
        </div>
    </#if>
    <#if events?size == 0>
        No events
    </#if>
    <div class="row gap-8">
        <button hx-get="/events/new-event-form?clubId=${club.id}" hx-target="#page" hx-swap="innerHTML">Add
            event
        </button>
    </div>
</div>

<div id="menu" class="row gap-8" hx-swap-oob="true">
    <button class="naked" hx-get="/clubs" hx-target="#page" hx-swap="innerHTML">Clubs</button>
    <button class="naked b" hx-get="/events" hx-target="#page" hx-swap="innerHTML">Events</button>
</div>
