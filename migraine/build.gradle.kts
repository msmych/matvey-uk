plugins {
    id("org.flywaydb.flyway") version "9.22.3"
}

val typesafeConfigVersion: String by project
val kitVersion: String by project
val slonVersion: String by project

dependencies {
    implementation("org.flywaydb:flyway-core:9.22.3")
    implementation("com.typesafe:config:$typesafeConfigVersion")
    implementation("uk.matvey:slon:$slonVersion")

    implementation(project(":telek"))
}
