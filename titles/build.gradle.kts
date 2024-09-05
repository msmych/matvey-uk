val kitVersion: String by project
val slonVersion: String by project
val junitVersion: String by project

dependencies {
    implementation("uk.matvey:kit:$kitVersion")
    implementation("uk.matvey:slon:$slonVersion")

    testImplementation(platform("org.junit:junit-bom:$junitVersion"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}
