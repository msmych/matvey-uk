val slonVersion: String by project
val coroutinesVersion: String by project
val typesafeConfigVersion: String by project

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
    implementation("com.typesafe:config:$typesafeConfigVersion")
    implementation("uk.matvey:slon:$slonVersion")

    implementation(project(":telek"))
    implementation(project(":drinki"))
}
