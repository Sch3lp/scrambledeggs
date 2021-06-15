package scrambledeggs.spring

import org.gradle.kotlin.dsl.dependencies

plugins {
    id("scrambledeggs.kotlin.common-conventions")
    id("scrambledeggs.spring.web-conventions")
    id("org.springframework.boot")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
}
