<#import "components/menu.ftl" as menu>

<html lang="en">
<head>
    <title>Falafel</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="/assets/style.css">
    <link rel="apple-touch-icon" sizes="180x180" href="${assets}/falafel-favicons/apple-touch-icon.png">
    <link rel="icon" type="image/png" sizes="32x32" href="${assets}/falafel-favicons/favicon-32x32.png">
    <link rel="icon" type="image/png" sizes="16x16" href="${assets}/falafel-favicons/favicon-16x16.png">
    <link rel="manifest" href="${assets}/falafel-favicons/site.webmanifest">
    <script src="https://unpkg.com/htmx.org@2.0.1"
            integrity="sha384-QWGpdj554B4ETpJJC9z+ZHJcA/i59TyjxEPXiiUgN2WmTyV5OEZWCD6gQhgkdpB/"
            crossorigin="anonymous"></script>
</head>
<body>
<div class="row gap-16">
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
        background-color: brown;
    }

    button:hover {
        background-color: brown;
    }

    input {
        padding: 8px 16px;
        font-size: 1em;
        border: lightgray 2px solid;
        border-radius: 4px;
    }

    button:hover, .click:hover {
        background-color: brown;
    }

    @keyframes spin {
        0% {
            transform: rotate(0deg);
        }
        100% {
            transform: rotate(360deg);
        }
    }

    .htmx-indicator {
        display: inline-block;
        animation: spin 2s linear infinite;
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
