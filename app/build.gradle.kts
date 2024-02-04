plugins {
    id("io.ktor.plugin") version "2.3.7"
}

dependencies {
    implementation("io.ktor:ktor-server-core")
    implementation("io.ktor:ktor-server-netty")
    implementation("io.ktor:ktor-server-freemarker")
}