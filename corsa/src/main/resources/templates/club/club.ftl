<div id="club-${club.id}">
    <a href="#" hx-get="/events?clubId=${club.id}" hx-target="#main">${club.name}</a>
    <button hx-delete="/clubs/${club.id}" hx-target="#club-${club.id}" hx-swap="delete">Remove</button>
</div>
