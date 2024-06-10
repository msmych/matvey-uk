val typesafeConfigVersion: String by project
val hikariCpVersion: String by project
val flywayVersion: String by project
val slonVersion: String by project
val kotlinLoggingVersion: String by project
val logbackClassicVersion: String by project
val postgresqlVersion: String by project
val testcontainersVersion: String by project

dependencies {
    implementation("com.zaxxer:HikariCP:$hikariCpVersion")
    implementation("org.flywaydb:flyway-core:$flywayVersion")
    implementation("uk.matvey:slon:$slonVersion")
    implementation("io.github.microutils:kotlin-logging-jvm:$kotlinLoggingVersion")
    implementation("ch.qos.logback:logback-classic:$logbackClassicVersion")
    implementation("org.postgresql:postgresql:$postgresqlVersion")
    implementation("com.typesafe:config:$typesafeConfigVersion")

    implementation(project(":dukt"))
    implementation(project(":telek"))

    testImplementation(platform("org.testcontainers:testcontainers-bom:$testcontainersVersion"))
    testImplementation("org.testcontainers:postgresql")
}