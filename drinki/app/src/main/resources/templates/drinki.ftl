<html lang="en">
<header>
    <script src="/htmx.min.js"/>
</header>
<body>
<h1>Drinki</h1>
<button hx-post="/drinks" hx-target="#drink-edit">
    + New drink
</button>
<div id="drink-edit"></div>
</body>
</html>