plugins {
    kotlin("jvm") version "2.0.0"
    kotlin("plugin.serialization") version "2.0.0"
}

repositories {
    mavenCentral()
}

java {
    sourceCompatibility = JavaVersion.VERSION_22
    targetCompatibility = JavaVersion.VERSION_22
}

kotlin {
    jvmToolchain(22)
}

val kitVersion: String by project

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.kotlin.plugin.serialization")

    repositories {
        mavenCentral()
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/msmych/kit")
            credentials {
                username = "GitHubPackages-RO"
                password = project.findProperty("ghPackagesRoToken") as? String ?: System.getenv("GH_PACKAGES_RO_TOKEN")
            }
        }
    }

    dependencies {
        implementation("io.github.microutils:kotlin-logging-jvm:2.0.11")
        implementation("ch.qos.logback:logback-classic:1.4.12")
        implementation("uk.matvey:kit:$kitVersion")

        testImplementation(platform("org.junit:junit-bom:5.10.2"))
        testImplementation("org.junit.jupiter:junit-jupiter")
        testRuntimeOnly("org.junit.platform:junit-platform-launcher")

        testImplementation("org.assertj:assertj-core:3.25.3")
    }

    java {
        sourceCompatibility = JavaVersion.VERSION_22
        targetCompatibility = JavaVersion.VERSION_22
    }

    kotlin {
        jvmToolchain(22)
    }

    tasks.test {
        useJUnitPlatform()
    }
}
