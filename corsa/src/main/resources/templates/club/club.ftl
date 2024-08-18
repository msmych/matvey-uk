<div id="club-${club.id}" class="row gap-8">
    <a href="#" hx-get="/clubs/${club.id}" hx-target="#main">${club.name}</a>
    <button class="naked" hx-delete="/clubs/${club.id}" hx-target="#club-${club.id}" hx-swap="delete">❌</button>
</div>
