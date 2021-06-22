package scrambledeggs.spring

import org.gradle.kotlin.dsl.dependencies

plugins {
    id("scrambledeggs.kotlin.common-conventions")
    id("scrambledeggs.spring.common-conventions")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.security:spring-security-oauth2-resource-server")
    implementation("org.springframework.security:spring-security-oauth2-jose")
}
