plugins {
    id("io.ktor.plugin") version "2.3.12"
}

val slonVersion: String by project
val utkaVersion: String by project
val telekVersion: String by project
val typesafeConfigVersion: String by project
val junitVersion: String by project

dependencies {
    implementation("uk.matvey:slon:$slonVersion")
    implementation("uk.matvey:utka:$utkaVersion")
    implementation("uk.matvey:telek:$telekVersion")
    implementation("com.typesafe:config:$typesafeConfigVersion")

    testImplementation(platform("org.junit:junit-bom:$junitVersion"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.shadowJar {
    enabled = false
}
