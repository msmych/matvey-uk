<html lang="en">
<head>
    <title>Begit events</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="/assets/style.css">
    <script src="/assets/htmx.min.js"></script>
</head>
<body>
<h1>Begit</h1>
<div hx-get="/me" hx-trigger="load" hx-swap="innerHTML"></div>
<h2>Clubs</h2>
<div hx-get="/clubs" hx-trigger="load" hx-swap="innerHTML"></div>
</body>
</html>
