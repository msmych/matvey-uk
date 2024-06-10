plugins {
    id("io.ktor.plugin") version "2.3.7"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

val typesafeConfigVersion: String by project
val slonVersion: String by project

dependencies {
    implementation("io.ktor:ktor-server-core")
    implementation("io.ktor:ktor-server-netty")
    implementation("io.ktor:ktor-server-freemarker")
    implementation("io.ktor:ktor-network-tls-certificates")
    implementation("org.flywaydb:flyway-core:9.22.3")
    implementation("com.zaxxer:HikariCP:5.1.0")
    implementation("uk.matvey:slon:$slonVersion")
    
    implementation("com.typesafe:config:$typesafeConfigVersion")
    
    implementation(project(":dukt"))
    implementation(project(":drinki"))
    implementation(project(":drinki:bot"))
    implementation(project(":migraine"))
    implementation(project(":telek"))
    
    testImplementation("io.ktor:ktor-client-cio")
}

application {
    mainClass.set("uk.matvey.app.AppKt")
}

tasks {
    shadowJar {
        manifest {
            attributes["Main-Class"] = "uk.matvey.app.AppKt"
        }
    }
}
