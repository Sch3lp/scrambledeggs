import org.gradle.kotlin.dsl.kotlin
import org.gradle.kotlin.dsl.version

plugins {
    id("scrambledeggs.kotlin-common-conventions")
    id("scrambledeggs.spring-common-conventions")
    id("org.springframework.boot") apply false
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
}

tasks.getByName<Jar>("jar") {
    enabled = true
}

tasks.withType<org.springframework.boot.gradle.tasks.bundling.BootJar> {
    classifier = "boot"
    mainClassName = "snarf"
}