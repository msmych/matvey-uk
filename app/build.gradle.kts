plugins {
    `java-test-fixtures`
    id("io.ktor.plugin") version "2.3.12"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

val typesafeConfigVersion: String by project
val slonVersion: String by project
val kitVersion: String by project
val junitVersion: String by project
val assertjVersion: String by project

dependencies {
    implementation("io.ktor:ktor-server-core")
    implementation("io.ktor:ktor-server-netty")
    implementation("io.ktor:ktor-server-freemarker")
    implementation("io.ktor:ktor-network-tls-certificates")
    implementation("uk.matvey:kit:$kitVersion")
    implementation("uk.matvey:slon:$slonVersion")

    implementation("com.typesafe:config:$typesafeConfigVersion")

    implementation(project(":migraine"))
    implementation(project(":telek"))

    testFixturesImplementation(platform("org.junit:junit-bom:$junitVersion"))
    testFixturesImplementation("org.junit.jupiter:junit-jupiter-api")
    testFixturesRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testFixturesImplementation("org.assertj:assertj-core:$assertjVersion")
    testFixturesImplementation("io.ktor:ktor-client-core")
    testFixturesImplementation("io.ktor:ktor-client-cio")

    testImplementation("io.ktor:ktor-client-cio")
}

application {
    mainClass.set("uk.matvey.app.AppKt")
}

tasks {
    shadowJar {
        manifest {
            attributes["Main-Class"] = "uk.matvey.app.AppKt"
        }
    }
}
