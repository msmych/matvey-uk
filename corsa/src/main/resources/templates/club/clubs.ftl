<h2>Clubs</h2>
<#list clubs as club>
    <div>
        <a href="#" hx-get="/events?clubId=${club.id}" hx-target="#main">${club.name}</a>
    </div>
</#list>
<div id="new-club">
    <button hx-get="/clubs/new-club-form" hx-target="#new-club" hx-swap="innerHTML">Add club</button>
</div>
