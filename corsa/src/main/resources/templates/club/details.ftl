<div class="col gap-8">
    <#if events?size gt 0>
        <t1>${club.name}</t1>
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
