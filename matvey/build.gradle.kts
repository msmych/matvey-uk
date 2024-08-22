val slonVersion: String by project
val telekVersion: String by project
val typesafeConfigVersion: String by project
val junitVersion: String by project

dependencies {
    implementation("uk.matvey:slon:$slonVersion")
    implementation("uk.matvey:telek:$telekVersion")
    implementation("com.typesafe:config:$typesafeConfigVersion")

//    implementation(project(":kino"))

    testImplementation(platform("org.junit:junit-bom:$junitVersion"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
