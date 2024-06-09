plugins {
    id("org.flywaydb.flyway") version "9.22.3"
}

dependencies {
    implementation("org.flywaydb:flyway-core:9.22.3")
    implementation("com.zaxxer:HikariCP:5.1.0")
    implementation("com.typesafe:config:1.4.3")
    implementation("uk.matvey:slon:0.0.2-RC2")
    
    implementation(project(":dukt"))
    implementation(project(":telek"))
}
