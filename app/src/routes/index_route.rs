use axum::response::Html;
use leptos::{ssr::render_to_string, *};

pub async fn index() -> Html<String> {
    Html(render_to_string(Index).into())
}

#[component]
fn Index() -> impl IntoView {
    let links = vec![
        ("Telegram", "https://t.me/msmych"),
        ("Instagram", "https://instagram.com/matveysmychkov"),
        (
            "LinkedIn",
            "https://linkedin.com/in/matvey-smychkov-743b21175",
        ),
        ("GitHub", "https://github.com/msmych"),
    ];
    view! {
        <head>
            <meta charset="UTF-8" />
            <link rel="stylesheet" href="assets/style.css" />
            <link rel="apple-touch-icon" sizes="180x180" href="assets/apple-touch-icon.png" />
            <link rel="icon" type="image/png" sizes="32x32" href="assets/favicon-32x32.png" />
            <link rel="icon" type="image/png" sizes="16x16" href="assets/favicon-16x16.png" />
            <link rel="manifest" href="assets/site.webmanifest" />
            <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Ubuntu&display=swap" />
            <script src="https://unpkg.com/htmx.org@1.9.8" integrity="sha384-rgjA7mptc2ETQqXoYC3/zJvkU7K/aP44Y+z7xQuJiVnB/422P/Ak+F/AqFR7E4Wr" crossorigin="anonymous"></script>
            <title>"Matvey"</title>
        </head>
        <body>
            <div class="block">
                <h1>"Matvey Smychkov"</h1>
                <p>"Software engineer living in London"</p>
                { links.iter().map(|(k, v)| view! { <Link title=(*k).into() url=(*v).into() /> }).collect_view() }
            </div>
        </body>
    }
}

#[component]
fn Link(title: String, url: String) -> impl IntoView {
    view! {
        <p>
            <a href={url}>{title}</a>
        </p>
    }
}
