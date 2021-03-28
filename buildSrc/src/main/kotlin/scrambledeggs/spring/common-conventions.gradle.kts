package scrambledeggs.spring

import gradle.kotlin.dsl.accessors._2e598aa7315da078f3d855fa3d56e434.dependencyManagement
import gradle.kotlin.dsl.accessors._2e598aa7315da078f3d855fa3d56e434.testImplementation
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.kotlin
import org.gradle.kotlin.dsl.repositories

plugins {
    id("scrambledeggs.kotlin.common-conventions")
    id("io.spring.dependency-management")
    kotlin("plugin.spring")
}

repositories {
    mavenCentral()
}

dependencyManagement {
    imports {
        mavenBom(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES)
    }
}

dependencies {
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}
