dependencies {
    api("com.zaxxer:HikariCP:5.1.0")
    api("org.flywaydb:flyway-core:9.22.3")
    api("org.postgresql:postgresql:42.6.0")

    implementation("com.typesafe:config:1.4.3")

    implementation(project(":dukt"))
}
