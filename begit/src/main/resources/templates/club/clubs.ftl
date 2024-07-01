<#list clubs as club>
    <div>
        ${club.name}
    </div>
    <h3>Events</h3>
    <div id="club-${club.id}-events" hx-get="/events?clubId=${club.id}" hx-trigger="load" hx-swap="innerHTML"></div>
</#list>
