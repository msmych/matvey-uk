plugins {
    id("org.flywaydb.flyway") version "9.22.3"
}

val typesafeConfigVersion: String by project

dependencies {
    implementation("com.zaxxer:HikariCP:5.1.0")
    implementation("com.typesafe:config:$typesafeConfigVersion")
    
    implementation(project(":dukt"))
    implementation(project(":telek"))
    implementation(project(":drinki"))
}
