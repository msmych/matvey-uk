<html lang="en">
<head>
    <title>Matvey</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="/assets/style.css">
    <link rel="apple-touch-icon" sizes="180x180" href="${assets}/favicons/apple-touch-icon.png">
    <link rel="icon" type="image/png" sizes="32x32" href="${assets}/favicons/favicon-32x32.png">
    <link rel="icon" type="image/png" sizes="16x16" href="${assets}/favicons/favicon-16x16.png">
    <link rel="manifest" href="${assets}/favicons/site.webmanifest">
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
                <div class="tab click" hx-get="/me" hx-target="#content">👤 ${account.name}</div>
            </#if>
            <#if !account?has_content>
                <a class="tab click" href="/login">Login</a>
            </#if>
        </div>
    </div>
    <div id="content" class="col gap-8">
        <div>
            Hi, my name is Matvey
        </div>
        <div>
            I live in London and work as a <a href="https://www.linkedin.com/in/matvey-smychkov-743b21175/">software
                engineer</a>
        </div>
        <div>
            I do some coding in my spare time as well. Currently, I'm working several open-source Kotlin libraries:
        </div>
        <div>
            <a href="https://github.com/msmych/slon">Slon</a>: lightweight library to work with Postgres
        </div>
        <div>
            <a href="https://github.com/msmych/telek">Telek</a>: Kotlin Telegram Bot API client
        </div>
        <div>
            <a href="https://github.com/msmych/kit">Kit</a>: misc utilities I find useful as an extension of
            standard library
        </div>
        <div>
            I also solved over 1000 problems on <a href="https://leetcode.com/u/msmych/">LeetCode</a>
        </div>
        <div>
            My favorite <a href="https://strava.app.link/VrrOjcw0KMb">sport activity</a> is running. I finished a
            marathon in less than 3:50:00
        </div>
        <div>
            You can reach me on <a href="https://t.me/msmych">Telegram</a>
        </div>
        <div>This website is being built Kotlin + Ktor + HTMX + Postgres + Gradle + GitHub + the libraries above</div>
        <div>
            👾
        </div>
    </div>
</div>
</body>
<style>
    #home {
        margin: 64px auto 0;
        width: 80%;
        min-width: 320px;
        max-width: 2048px;
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
