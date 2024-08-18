<div id="event-${event.id}" class="row gap-8">
    <div class="click" hx-get="/events/${event.id}" hx-target="#main">${event.name}</div>
    <button class="naked" hx-delete="/events/${event.id}" hx-target="#event-${event.id}" hx-swap="delete">🗑️</button>
</div>
