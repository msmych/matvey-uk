val slonVersion: String by project
val typesafeConfigVersion: String by project
val junitVersion: String by project

dependencies {
    implementation("uk.matvey:slon:$slonVersion")
    implementation("com.typesafe:config:$typesafeConfigVersion")

    testImplementation(platform("org.junit:junit-bom:$junitVersion"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
