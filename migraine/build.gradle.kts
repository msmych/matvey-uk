val kitVersion: String by project
val slonVersion: String by project

dependencies {
    implementation("uk.matvey:kit:$kitVersion")
    implementation("uk.matvey:slon:$slonVersion")

    implementation(project(":telek"))
}
