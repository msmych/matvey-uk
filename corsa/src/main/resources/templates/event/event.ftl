<div id="event-${event.id}" class="row gap16">
    <a href="#" hx-get="/events/${event.id}" hx-target="#main">${event.name}</a>
    <button hx-delete="/events/${event.id}" hx-target="#event-${event.id}" hx-swap="delete">X</button>
</div>
