dependencies {
    implementation(platform("org.testcontainers:testcontainers-bom:1.19.8"))
    
    implementation("org.postgresql:postgresql:42.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0-RC2")
    implementation("com.zaxxer:HikariCP:5.1.0")
    
    testImplementation("org.testcontainers:postgresql")
}
