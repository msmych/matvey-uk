plugins {
    id("io.ktor.plugin") version "2.3.12"
}

val voronVersion: String by project

dependencies {
    implementation("uk.matvey:voron:$voronVersion")
}