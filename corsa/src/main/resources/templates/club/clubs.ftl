<h2>Clubs</h2>
<#list clubs as club>
    <div>
        <a href="#" hx-get="/events?clubId=${club.id}" hx-target="#main">${club.name}</a>
    </div>
</#list>
