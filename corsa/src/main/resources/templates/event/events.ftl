<h2>Events</h2>
<#list events as event>
    <div>
        <a href="#" hx-get="/event/${event.id}" hx-target="#main">${event.name}</a>
    </div>
</#list>