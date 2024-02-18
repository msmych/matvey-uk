plugins {
    id("io.ktor.plugin") version "2.3.7"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

dependencies {
    implementation("io.ktor:ktor-server-core")
    implementation("io.ktor:ktor-server-netty")
    implementation("io.ktor:ktor-server-freemarker")
    
    implementation("com.typesafe:config:1.4.3")
    
    implementation(project(":dukt"))
    
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
