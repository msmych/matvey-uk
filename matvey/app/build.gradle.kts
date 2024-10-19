import com.github.jengelman.gradle.plugins.shadow.transformers.ServiceFileTransformer

plugins {
    id("io.ktor.plugin") version "3.0.0"
}

val slonVersion: String by project
val utkaVersion: String by project
val telekVersion: String by project
val junitVersion: String by project

repositories {
    maven {
        name = "SlonPackages"
        url = uri("https://maven.pkg.github.com/msmych/slon")
        credentials {
            username = "matvey-uk"
            password = project.findProperty("ghPackagesRoToken") as? String ?: System.getenv("GH_PACKAGES_RO_TOKEN")
        }
    }
}

dependencies {
    implementation("uk.matvey:slon:$slonVersion")
    implementation("uk.matvey:utka:$utkaVersion")
    implementation("uk.matvey:telek:$telekVersion")

    implementation(project(":matvey:common"))
    implementation(project(":falafel"))
    implementation(project(":tmdb-client"))

    testImplementation(platform("org.junit:junit-bom:$junitVersion"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

application {
    mainClass = "uk.matvey.app.AppKt"
}

tasks.shadowJar {
    transform(ServiceFileTransformer::class.java)
}
