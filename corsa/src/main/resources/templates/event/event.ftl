<div id="event-${event.id}" class="row gap-8">
    <a href="#" hx-get="/events/${event.id}" hx-target="#main">${event.name}</a>
    <button class="naked" hx-delete="/events/${event.id}" hx-target="#event-${event.id}" hx-swap="delete">🗑️</button>
</div>
