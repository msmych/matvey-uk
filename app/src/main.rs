use app::routes::index_route::index;
use axum::{routing::get, Router};
use tower_http::{services::ServeDir, trace::TraceLayer};
use tracing_subscriber::{layer::SubscriberExt, util::SubscriberInitExt};

#[tokio::main]
async fn main() {
    let args: Vec<String> = std::env::args().collect();
    let port: u16 = args[1].parse().unwrap();
    tracing_subscriber::registry()
        .with(
            tracing_subscriber::EnvFilter::try_from_default_env()
                .unwrap_or_else(|_| "app=debug,tower_http=debug,axum::rejection=trace".into()),
        )
        .with(tracing_subscriber::fmt::layer())
        .init();
    axum::serve(
        tokio::net::TcpListener::bind(format!("0.0.0.0:{port}"))
            .await
            .unwrap(),
        Router::new()
            .route("/healthcheck", get(|| async { "OK" }))
            .route("/", get(index))
            .nest_service("/assets", ServeDir::new("app/assets"))
            .layer(TraceLayer::new_for_http())
            .into_make_service(),
    )
    .await
    .unwrap();
}
