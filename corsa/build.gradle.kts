plugins {
    `java-test-fixtures`
    id("io.ktor.plugin") version "2.3.12"
}

val slonVersion: String by project
val telekVersion: String by project
val voronVersion: String by project
val junitVersion: String by project
val assertjVersion: String by project

dependencies {
    implementation("uk.matvey:slon:$slonVersion")
    implementation("uk.matvey:telek:$telekVersion")
    implementation("uk.matvey:voron:$voronVersion")

    testImplementation(platform("org.junit:junit-bom:$junitVersion"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("io.ktor:ktor-server-test-host")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.assertj:assertj-core:$assertjVersion")

    testImplementation(testFixtures("uk.matvey:slon:$slonVersion"))
}
