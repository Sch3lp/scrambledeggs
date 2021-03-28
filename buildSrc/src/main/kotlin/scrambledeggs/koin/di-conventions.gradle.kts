package scrambledeggs.koin

import gradle.kotlin.dsl.accessors._1fedb3f582a8ee457b78c12de5a29dae.api
import gradle.kotlin.dsl.accessors._1fedb3f582a8ee457b78c12de5a29dae.implementation
import gradle.kotlin.dsl.accessors._1fedb3f582a8ee457b78c12de5a29dae.testImplementation
import org.gradle.kotlin.dsl.`java-library`
import org.gradle.kotlin.dsl.`java-test-fixtures`
import org.gradle.kotlin.dsl.dependencies

plugins {
    id("scrambledeggs.kotlin.common-conventions")
    `java-library`
    `java-test-fixtures`
}

val koinVersion = "2.2.0-rc-4"

repositories {
    jcenter()
}

dependencies {

    api("org.slf4j:slf4j-api:1.7.30")
    implementation("org.koin:koin-core:$koinVersion")
    implementation("org.koin:koin-ktor:$koinVersion")
    implementation("org.koin:koin-logger-slf4j:$koinVersion")
    testImplementation("org.koin:koin-test:$koinVersion")

}
