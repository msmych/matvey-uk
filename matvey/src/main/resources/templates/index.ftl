<html lang="en">
<head>
    <title>Matvey</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="/assets/style.css">
    <script src="https://unpkg.com/htmx.org@2.0.1"
            integrity="sha384-QWGpdj554B4ETpJJC9z+ZHJcA/i59TyjxEPXiiUgN2WmTyV5OEZWCD6gQhgkdpB/"
            crossorigin="anonymous"></script>
</head>
<body>
<div id="home" class="col">
    <div class="row split">
        <div class="row">
            <a class="tab" href="/">Home</a>
            <div class="tab">Kino</div>
        </div>
        <div class="row">
            <#if account?has_content>
                <div class="tab" hx-get="/me" hx-target="#content">👤 ${account.name} </div>
            </#if>
            <#if !account?has_content>
                <a class="tab" href="/login">Login</a>
            </#if>
        </div>
    </div>
    <div id="content" class="col">
        <h1>👋</h1>
    </div>
</div>
</body>
<style>
    #home {
        margin: 64px auto 0;
        width: 80%;
        min-width: 400px;
        max-width: 2000px;
    }

    .tab {
        padding: 8px 16px;
    }
</style>
