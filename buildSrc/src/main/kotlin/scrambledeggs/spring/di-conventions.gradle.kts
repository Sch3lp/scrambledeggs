package scrambledeggs.spring

import gradle.kotlin.dsl.accessors._2e598aa7315da078f3d855fa3d56e434.dependencyManagement
import gradle.kotlin.dsl.accessors._2e598aa7315da078f3d855fa3d56e434.implementation
import org.gradle.kotlin.dsl.dependencies

plugins {
    id("scrambledeggs.kotlin.common-conventions")
    id("scrambledeggs.spring.common-conventions")
}

dependencies {
    val springCtxVersion = dependencyManagement.managedVersions["org.springframework:spring-context"]
    implementation("org.springframework:spring-context:$springCtxVersion")
}
