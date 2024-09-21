<h2>Clubs</h2>
<#list clubs as club>
    <h3>${club.name}</h3>
</#list>
<button hx-get="/falafel/clubs/new-club-form" hx-target="#content">New club</button>
