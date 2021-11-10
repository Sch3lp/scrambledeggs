package scrambledeggs.spring

import org.gradle.kotlin.dsl.dependencies

plugins {
    id("scrambledeggs.kotlin.common")
    id("scrambledeggs.spring.common")
}

dependencies {
    val springCtxVersion = dependencyManagement.managedVersions["org.springframework:spring-context"]
    implementation("org.springframework:spring-context:$springCtxVersion")
}
