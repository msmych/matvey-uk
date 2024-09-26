plugins {
    id("io.ktor.plugin") version "2.3.12"
}

val utkaVersion: String by project

dependencies {
    implementation("uk.matvey:utka:$utkaVersion")
}
