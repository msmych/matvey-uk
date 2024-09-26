<div class="col gap-16">
    <div class="t1">${name}</div>
    <a href="/logout">Logout</a>
    <button class="primary"
            hx-get="/accounts/edit-details-form"
            hx-target="#content">
        Update details
    </button>
</div>
<#-- OOB -->
<div id="account-tab" class="tab click" hx-get="/me" hx-target="#content" hx-swap-oob="true">👤 ${name}</div>
