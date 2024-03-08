<html lang="en">
<head>
    <title>Matvey</title>
    <script src="https://unpkg.com/htmx.org@1.9.10"
            integrity="sha384-D1Kt99CQMDuVetoL1lrYwg5t+9QdHe7NLX/SoJYkXDFfX37iInKRy5xLSi8nO7UC"
            crossorigin="anonymous"></script>
    <link rel="stylesheet" href="/assets/style.css">
</head>
<body>
<div class="main v-stack gap-32">
    <div class="h-stack gap-16">
        <a href="/" class="h-1">🏠</a>
        <div class="h-1">Matvey Smychkov</div>
    </div>
    <div id="main-content" class="v-stack gap-16">
        <h2>👋 Hi there</h2>
        <div>
            I'm a software engineer. Check out <a href="#" hx-get="/tech" hx-target="#main-content">tech page</a>
            to learn more.
        </div>
    </div>
</div>
</body>
</html>
