plugins {
    id("scrambledeggs.kotlin-conventions")
    id("org.springframework.boot") version "2.4.0"
    id("io.spring.dependency-management") version "1.0.10.RELEASE"
    kotlin("plugin.spring") version "1.4.21"
}

apply(plugin = "io.spring.dependency-management")

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    project(":adapter:rest-api")
    project(":adapter:eventsourcing")
    project(":adapter:rdbms")
    project(":domain:core")
    project(":domain:core-api")
    project(":infra")
    project(":ui")
}

tasks.withType<org.springframework.boot.gradle.tasks.bundling.BootJar> {
    baseName = "scrambledeggs-webapp"
}

tasks.processResources {
    dependsOn(":ui:build")
    from ("../ui/build/elm") {
        into("static")
    }
}