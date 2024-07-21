plugins {
    id("io.ktor.plugin") version "2.3.7"
}

val typesafeConfigVersion: String by project
val slonVersion: String by project

dependencies {
    implementation("io.ktor:ktor-server-core")
    implementation("io.ktor:ktor-server-netty")
    implementation("io.ktor:ktor-server-freemarker")
    implementation("uk.matvey:slon:$slonVersion")

    implementation("com.typesafe:config:$typesafeConfigVersion")
    
    implementation(project(":drinki"))
}
