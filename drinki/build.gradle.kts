val kitVersion: String by project
val slonVersion: String by project

dependencies {
    implementation("org.flywaydb:flyway-core:9.22.3")
    implementation("uk.matvey:slon:$slonVersion")
}
