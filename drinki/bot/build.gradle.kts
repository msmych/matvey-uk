plugins {
    id("org.flywaydb.flyway") version "9.22.3"
}

val kitVersion: String by project
val slonVersion: String by project
val kotlinxCoroutinesVersion: String by project
val typesafeConfigVersion: String by project

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxCoroutinesVersion")
    implementation("com.typesafe:config:$typesafeConfigVersion")
    implementation("uk.matvey:slon:$slonVersion")

    implementation(project(":telek"))
    implementation(project(":drinki"))
}
