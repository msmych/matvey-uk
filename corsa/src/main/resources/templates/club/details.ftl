<h2>${club.name}</h2>
<h2>Events</h2>
<#list events as event>
    <div>
        <a href="#" hx-get="/events/${event.id}" hx-target="#main">${event.name}</a>
    </div>
</#list>
<div id="new-event">
    <button hx-get="/events/new-event-form?clubId=${club.id}" hx-target="#new-event" hx-swap="innerHTML">Add event
    </button>
</div>