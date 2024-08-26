<div class="col gap-16">
    <div class="click t1">${name}</div>
    <a href="/logout">Logout</a>
    <button class="primary"
            hx-get="/accounts/edit-details-form"
            hx-target="#content">
        Update details
    </button>
</div>
