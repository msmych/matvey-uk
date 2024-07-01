plugins {
    id("io.ktor.plugin") version "3.0.0-beta-1"
    application
}

val typesafeConfigVersion: String by project
val hikariCpVersion: String by project
val flywayVersion: String by project
val slonVersion: String by project
val kotlinLoggingVersion: String by project
val logbackClassicVersion: String by project
val postgresqlVersion: String by project
val testcontainersVersion: String by project
val mockkVersion: String by project

dependencies {
    implementation("ch.qos.logback:logback-classic:$logbackClassicVersion")
    implementation("com.typesafe:config:$typesafeConfigVersion")
    implementation("com.zaxxer:HikariCP:$hikariCpVersion")
    implementation("io.github.microutils:kotlin-logging-jvm:$kotlinLoggingVersion")
    implementation("io.ktor:ktor-network-tls-certificates")
    implementation("io.ktor:ktor-server-core")
    implementation("io.ktor:ktor-server-freemarker")
    implementation("io.ktor:ktor-server-netty")
    implementation("org.flywaydb:flyway-core:$flywayVersion")
    implementation("org.postgresql:postgresql:$postgresqlVersion")
    implementation("uk.matvey:slon:$slonVersion")

    implementation(project(":dukt"))
    implementation(project(":telek"))

    testImplementation(platform("org.testcontainers:testcontainers-bom:$testcontainersVersion"))
    testImplementation("org.testcontainers:postgresql")
    testImplementation("io.mockk:mockk:$mockkVersion")
}

application {
    mainClass.set("uk.matvey.begit.AppKt")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=true")
}
