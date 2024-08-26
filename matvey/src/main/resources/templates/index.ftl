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
<div id="home" class="col gap-16">
    <div id="menu" class="row split">
        <div class="row gap-8">
            <a class="tab click active" href="/">Home</a>
        </div>
        <div class="row gap-8">
            <#if account?has_content>
                <div class="tab click" hx-get="/me" hx-target="#content">👤 ${account.name} </div>
            </#if>
            <#if !account?has_content>
                <a class="tab click" href="/login">Login</a>
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
        border-radius: 8px;
    }

    button {
        padding: 8px 16px;
        border-radius: 8px;
        font-size: 1em;
        max-width: 256px;
        cursor: pointer;
        background: transparent;
        border: none;
    }

    button.primary {
        color: white;
        background-color: darkcyan;
    }

    input {
        padding: 8px 16px;
        border: lightgray 2px solid;
        border-radius: 8px;
        font-size: 1em;
    }
</style>
