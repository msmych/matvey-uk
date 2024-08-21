<div class="col gap-8">
    <#if clubs?size gt 0>
        <div class="col gap-8">
            <#list clubs as club>
                <#include "club.ftl">
            </#list>
        </div>
    </#if>
    <#if clubs?size == 0>
        <div>No clubs</div>
    </#if>
    <div class="row gap-8">
        <button hx-get="/clubs/new-club-form" hx-target="#page" hx-swap="innerHTML">Add club</button>
    </div>
</div>

<div id="menu" class="row gap-8" hx-swap-oob="true">
    <button class="naked b" hx-get="/clubs" hx-target="#page" hx-swap="innerHTML">Clubs</button>
    <button class="naked" hx-get="/events" hx-target="#page" hx-swap="innerHTML">Events</button>
</div>
