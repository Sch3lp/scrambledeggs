package scrambledeggs.spring

import gradle.kotlin.dsl.accessors._13f50cb83ed9fe49f0bc787f9febfffd.implementation
import org.gradle.kotlin.dsl.dependencies

plugins {
    id("scrambledeggs.kotlin.common-conventions")
    id("scrambledeggs.spring.web-conventions")
    id("org.springframework.boot")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
}
