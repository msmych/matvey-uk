<html lang="en">
<head>
    <title>Begit events</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="/assets/style.css">
    <script src="/assets/htmx.min.js"></script>
</head>
<body>
<h1>Begit</h1>
<div>
    ${athlete.name}
</div>
<h2>Events</h2>
<#list events as event>
    <div>
        ${event.title}
    </div>
</#list>
<h2>Clubs 22</h2>
<#list clubs as club>
    <div>
        ${club.name}
    </div>
</#list>
</body>
</html>
