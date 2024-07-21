<html lang="en">
<head>
    <title>Matvey</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="/assets/style.css">
    <link rel="apple-touch-icon" sizes="180x180" href="${assets}/favicons/apple-touch-icon.png">
    <link rel="icon" type="image/png" sizes="32x32" href="${assets}/favicons/favicon-32x32.png">
    <link rel="icon" type="image/png" sizes="16x16" href="${assets}/favicons/favicon-16x16.png">
    <link rel="manifest" href="${assets}/favicons/site.webmanifest">
    <link rel="preload" href="Mona-Sans.woff2" as="font" type="font/woff2" crossorigin>
    <script src="https://unpkg.com/htmx.org@2.0.1" integrity="sha384-QWGpdj554B4ETpJJC9z+ZHJcA/i59TyjxEPXiiUgN2WmTyV5OEZWCD6gQhgkdpB/" crossorigin="anonymous"></script>
</head>
<body>
<div class="main v-stack gap-32">
    <div class="h-stack gap-16">
        <a href="/" class="h-1">🏠</a>
        <a class="h-1" href="#" hx-get="/tech" hx-target="#main-content">⚙️</a>
    </div>
    <div id="main-content" class="v-stack gap-16">
        <h2>👋 Hi there</h2>
        <div>
            My name is Matvey.
            Not much is going on here yet, but I'll be adding more things over time.
        </div>
        <div>
            I'm a software engineer.
            Check out <a href="#" hx-get="/tech" hx-target="#main-content">tech page</a> to learn more.
        </div>
    </div>
</div>
</body>
</html>
