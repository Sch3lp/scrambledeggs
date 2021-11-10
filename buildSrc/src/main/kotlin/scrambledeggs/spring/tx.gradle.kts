package scrambledeggs.spring

import org.gradle.kotlin.dsl.dependencies

plugins {
    id("scrambledeggs.kotlin.common")
    id("scrambledeggs.spring.common")
}

dependencies {
    val springTxVersion = dependencyManagement.managedVersions["org.springframework:spring-tx"]
    implementation("org.springframework:spring-tx:$springTxVersion")
}
