<div id="club-${club.id}" class="row gap16">
    <a href="#" hx-get="/clubs/${club.id}" hx-target="#main">${club.name}</a>
    <button hx-delete="/clubs/${club.id}" hx-target="#club-${club.id}" hx-swap="delete">X</button>
</div>
