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
        <button class="primary"
                hx-get="/clubs/new-club-form"
                hx-target="#page"
                hx-swap="innerHTML"
                hx-indicator=".htmx-indicator"
                hx-disabled-elt="this"
        >
            Add club
        </button>
        <div class="htmx-indicator">👾</div>
    </div>
</div>
