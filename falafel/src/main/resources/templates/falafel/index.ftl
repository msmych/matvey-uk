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
    <script src="https://unpkg.com/htmx-ext-sse@2.2.2/sse.js"></script>
</head>
<body>
<div hx-ext="sse"
     sse-connect="/falafel/me/events"
     sse-swap="message"
     hx-target="#current-balance"
     hx-swap="innerHTML"
></div>
<div class="row gap-16">
    <div id="menu" class="col menu gap-16">
        <#if account?has_content>
            <button class="tab click"
                    hx-get="/falafel/me"
                    hx-target="#content"
                    hx-push-url="true"
            >
                <span id="menu-tab-account">${account.name}</span>
                <span>🍿</span>
                <span id="current-balance">${account.currentBalance}</span>
            </button>
        <#else>
            <a class="tab click" href="/login">
                <span id="menu-tab-account">Login</span>
            </a>
        </#if>
        <button class="tab"
                hx-get="/falafel/clubs"
                hx-target="#content"
                hx-push-url="true"
        >
            <span id="menu-tab-clubs">Clubs</span>
        </button>
        <button class="tab"
                hx-get="/falafel/titles"
                hx-target="#content"
                hx-push-url="true"
        >
            <span id="menu-tab-titles" style="font-weight: bold">🎞️ Titles</span>
        </button>
        <button class="tab"
                hx-get="/falafel/tmdb"
                hx-target="#content"
                hx-push-url="true"
        >
            <span id="menu-tab-tmdb">🗄️ TMDb</span>
        </button>
        <button class="tab"
                hx-get="/falafel/tags"
                hx-target="#content"
                hx-push-url="true"
        >
            <span id="menu-tab-tags">🏷️ Tags</span>
        </button>
    </div>
    <div id="content">
        <#if loadPage?has_content>
            <div hx-get="${loadPage}" hx-trigger="load delay:100ms" hx-swap="outerHTML"></div>
        </#if>
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

    .menu {
        flex-basis: 10em;
    }

    .tab.active {
        font-weight: bold;
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
