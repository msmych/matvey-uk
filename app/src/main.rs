use app::routes::{demo_route::demo, index_route::index};
use axum::{
    routing::{get, post},
    Router,
};
use tower_http::services::ServeDir;

#[tokio::main]
async fn main() {
    axum::Server::bind(&std::net::SocketAddr::from(([127, 0, 0, 1], 3000)))
        .serve(
            Router::new()
                .route("/healthcheck", get(|| async { "OK" }))
                .route("/", get(index))
                .nest_service("/assets", ServeDir::new("app/assets"))
                .into_make_service(),
        )
        .await
        .unwrap();
}
