<html lang="en">
<head>
    <title>Matvey</title>
    <script src="https://unpkg.com/htmx.org@1.9.10"
            integrity="sha384-D1Kt99CQMDuVetoL1lrYwg5t+9QdHe7NLX/SoJYkXDFfX37iInKRy5xLSi8nO7UC"
            crossorigin="anonymous"></script>
</head>
<body>
<div class="container v-stack gap-32">
    <a href="/" class="home-button">Matvey Smychkov</a>
    <div id="content" class="v-stack gap-16">
        <h2>Hi there</h2>
        <div>
            I'm a software engineer. Check out <a href="#" hx-get="/tech" hx-target="#content">tech page</a> to learn more.
        </div>
    </div>
</div>
</body>
</html>
<style>
    body {
        font-family: "Trebuchet MS", sans-serif;
        font-size: 18px;
    }

    .home-button {
        display: block;
        font-size: 2em;
        font-weight: bold;
        text-decoration: none;
        color: black;
    }

    .container {
        margin: 60px auto 0;
        width: 80%;
        min-width: 400px;
        max-width: 2000px;
    }

    .v-stack {
        display: flex;
        flex-direction: column;
    }

    .gap-16 {
        gap: 16px;
    }

    .gap-32 {
        gap: 32px;
    }
</style>
