<html lang="en">
<head>
    <title>Falafel</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="/assets/style.css">
    <script src="https://unpkg.com/htmx.org@2.0.1"
            integrity="sha384-QWGpdj554B4ETpJJC9z+ZHJcA/i59TyjxEPXiiUgN2WmTyV5OEZWCD6gQhgkdpB/"
            crossorigin="anonymous"></script>
</head>
<body>
<div class="row gap-16">
    <div class="col menu gap-16">
        <div class="t1" style="padding-left: 16px">Falafel</div>
        <#if account?has_content>
            <button id="account-tab" hx-get="/me" hx-target="#content">
                ${account.name} 🍿${account.balanceQuantity}</button>
        </#if>
        <#if !account?has_content>
            <a class="tab click" href="/login">Login</a>
        </#if>
        <button hx-get="/falafel/clubs" hx-target="#content">Clubs</button>
        <button hx-get="/falafel/titles" hx-target="#content">Titles</button>
    </div>
    <div id="content">
    </div>
</div>
</body>
<style>
    body {
        font-family: "Mona Sans", sans-serif;
        background-color: black;
        color: white;
    }

    .menu {
        flex-basis: 10em;
    }

    button {
        color: white;
        padding: 8px 16px;
        border-radius: 8px;
        font-size: 1em;
        max-width: 256px;
        cursor: pointer;
        background: transparent;
        border: none;
    }

    button:hover {
        background-color: midnightblue;
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
