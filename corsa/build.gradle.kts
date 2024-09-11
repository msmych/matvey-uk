plugins {
    id("io.ktor.plugin") version "2.3.12"
}

val slonVersion: String by project
val telekVersion: String by project
val utkaVersion: String by project
val kitVersion: String by project
val junitVersion: String by project
val assertjVersion: String by project

dependencies {
    implementation("uk.matvey:slon:$slonVersion")
    implementation("uk.matvey:telek:$telekVersion")
    implementation("uk.matvey:utka:$utkaVersion")

    testImplementation(platform("org.junit:junit-bom:$junitVersion"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")

    testImplementation("io.ktor:ktor-server-test-host")
    testImplementation("org.assertj:assertj-core:$assertjVersion")

    testImplementation(testFixtures("uk.matvey:slon:$slonVersion"))
}

tasks.shadowJar {
    enabled = false
}
