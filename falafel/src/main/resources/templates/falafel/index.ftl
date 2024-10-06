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
        <#import "components/menu.ftl" as menu>
        <@menu.menu account=account/>
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

    button.primary {
        background-color: midnightblue;
    }

    button:hover {
        background-color: midnightblue;
    }

    input {
        padding: 8px 16px;
        font-size: 1em;
        border: lightgray 2px solid;
        border-radius: 4px;
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
