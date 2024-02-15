<html lang="en">
<head>
    <script src="/assets/htmx.min.js"></script>
    <title>DRINKI</title>
</head>
<body>
<h1>Drinki</h1>
<div>
    <button hx-post="/drinks" hx-target="#drink-edit">
        + New drink
    </button>
</div>
<div id="drink-edit"></div>
</body>
</html>
