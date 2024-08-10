plugins {
    id("io.ktor.plugin") version "3.0.0-beta-1"
    application
}

val typesafeConfigVersion: String by project
val kitVersion: String by project
val slonVersion: String by project
val kotlinLoggingVersion: String by project
val logbackClassicVersion: String by project
val testcontainersVersion: String by project
val junitVersion: String by project
val mockkVersion: String by project
val assertjVersion: String by project
val coroutinesVersion: String by project
val kotestVersion: String by project

dependencies {
    implementation("com.typesafe:config:$typesafeConfigVersion")
    implementation("io.ktor:ktor-network-tls-certificates")
    implementation("io.ktor:ktor-server-core")
    implementation("io.ktor:ktor-server-freemarker")
    implementation("io.ktor:ktor-server-netty")
    implementation("uk.matvey:kit:$kitVersion")
    implementation("uk.matvey:slon:$slonVersion")

    implementation(project(":telek"))

    testImplementation(platform("org.junit:junit-bom:$junitVersion"))
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testImplementation(testFixtures("uk.matvey:slon:$slonVersion"))
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutinesVersion")
    testImplementation("io.mockk:mockk:$mockkVersion")
    testImplementation("org.assertj:assertj-core:$assertjVersion")
    testImplementation(platform("org.testcontainers:testcontainers-bom:$testcontainersVersion"))
    testImplementation("org.testcontainers:postgresql")
}

application {
    mainClass.set("uk.matvey.begit.AppKt")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=true")
}
