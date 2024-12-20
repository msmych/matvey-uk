plugins {
    id("io.ktor.plugin") version "3.0.0"
}

val assertjVersion: String by project
val coroutinesVersion: String by project
val utkaVersion: String by project

dependencies {
    implementation("uk.matvey:utka:$utkaVersion")

    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutinesVersion")
    testImplementation("org.assertj:assertj-core:$assertjVersion")
}

tasks.shadowJar {
    enabled = false
}
