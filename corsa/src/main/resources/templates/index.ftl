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
<div id="home">
    <h1 class="click" hx-get="/clubs" hx-target="#main" hx-swap="innerHTML">Corsa</h1>
    <div id="me" hx-get="/me" hx-trigger="load" hx-swap="innerHTML"></div>
    <div id="main" hx-get="/clubs" hx-trigger="load" hx-swap="innerHTML"></div>
</div>
</body>
<style>
    #home {
        margin: 60px auto 0;
        width: 80%;
        min-width: 400px;
        max-width: 2000px;
    }
</style>
<script>
    document.addEventListener('DOMContentLoaded', () => {
        document.body.addEventListener('htmx:responseError', e => {
            if (e.detail.xhr.status === 401) {
                window.location.href = '/login'
            }
        })
    })
</script>
</html>
