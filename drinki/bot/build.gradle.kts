plugins {
    id("org.flywaydb.flyway") version "9.22.3"
}

dependencies {
    implementation("com.typesafe:config:1.4.3")

    implementation(project(":dukt"))
    implementation(project(":postal"))
    implementation(project(":telek"))
    implementation(project(":drinki"))
}
