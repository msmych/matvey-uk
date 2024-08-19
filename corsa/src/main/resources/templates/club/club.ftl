<div id="club-${club.id}" class="row gap-8">
    <div class="click" hx-get="/clubs/${club.id}" hx-target="#main">${club.name}</div>
    <button class="naked" hx-delete="/clubs/${club.id}" hx-target="#club-${club.id}" hx-swap="delete">🗑️</button>
</div>
