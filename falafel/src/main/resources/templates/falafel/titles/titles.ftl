<h2>Titles</h2>
<#list titles as title>
    <h3>🎞️ ${title.title}</h3>
</#list>
<button hx-get="/falafel/titles/new-title-form" hx-target="#content">New title</button>
