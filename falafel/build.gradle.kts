plugins {
    id("io.ktor.plugin") version "3.0.0"
}

val kitVersion: String by project
val utkaVersion: String by project
val slonVersion: String by project
val junitVersion: String by project
val quartzVersion: String by project

dependencies {
    implementation("uk.matvey:kit:$kitVersion")
    implementation("uk.matvey:utka:$utkaVersion")
    implementation("uk.matvey:slon:$slonVersion")
    implementation("org.quartz-scheduler:quartz:$quartzVersion")

    implementation(project(":matvey:common"))
    implementation(project(":tmdb-client"))

    testImplementation(platform("org.junit:junit-bom:$junitVersion"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

application {
    mainClass = "uk.matvey.falafel.AppKt"
}
