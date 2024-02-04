plugins {
    id("org.flywaydb.flyway") version "9.22.3"
}

dependencies {
    implementation("com.typesafe:config:1.4.3")
    implementation("io.github.microutils:kotlin-logging-jvm:2.0.11")
    implementation("ch.qos.logback:logback-classic:1.4.12")
    implementation("com.zaxxer:HikariCP:5.1.0")
    implementation("org.flywaydb:flyway-core:9.22.3")

    runtimeOnly("org.postgresql:postgresql:42.6.0")

    implementation(project(":dukt"))
    implementation(project(":postal"))
    implementation(project(":telek"))
}
