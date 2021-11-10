package scrambledeggs.spring

import org.gradle.kotlin.dsl.dependencies

plugins {
    id("scrambledeggs.kotlin.common")
    id("scrambledeggs.spring.web")
    id("org.springframework.boot")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
}
