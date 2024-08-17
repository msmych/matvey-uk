<html lang="en">
<head>
    <title>Corsa</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="/assets/style.css">
    <link rel="preload" href="Mona-Sans.woff2" as="font" type="font/woff2" crossorigin>
    <script src="https://unpkg.com/htmx.org@2.0.1"
            integrity="sha384-QWGpdj554B4ETpJJC9z+ZHJcA/i59TyjxEPXiiUgN2WmTyV5OEZWCD6gQhgkdpB/"
            crossorigin="anonymous"></script>
</head>
<body>
<div class="main">
    <h1>Corsa</h1>
    <div id="main" hx-get="/clubs" hx-trigger="load" hx-swap="innerHTML"></div>
</div>
</body>
</html>