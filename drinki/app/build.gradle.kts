plugins {
    id("io.ktor.plugin") version "2.3.7"
}

dependencies {
    implementation("io.ktor:ktor-server-core")
    implementation("io.ktor:ktor-server-netty")
    implementation("io.ktor:ktor-server-freemarker")
    implementation("com.zaxxer:HikariCP:5.1.0")
    
    implementation("com.typesafe:config:1.4.3")
    
    implementation(project(":drinki"))
}
