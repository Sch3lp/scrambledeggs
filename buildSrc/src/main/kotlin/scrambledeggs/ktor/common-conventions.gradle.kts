package scrambledeggs.ktor

import gradle.kotlin.dsl.accessors._1fedb3f582a8ee457b78c12de5a29dae.api
import gradle.kotlin.dsl.accessors._1fedb3f582a8ee457b78c12de5a29dae.implementation
import gradle.kotlin.dsl.accessors._1fedb3f582a8ee457b78c12de5a29dae.testImplementation
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.`java-library`
import org.gradle.kotlin.dsl.`java-test-fixtures`
import org.gradle.kotlin.dsl.dependencies

plugins {
    id("scrambledeggs.kotlin.common-conventions")
    `java-library`
    `java-test-fixtures`
}

dependencies {

    api("org.slf4j:slf4j-api:1.7.30")

    implementation("io.ktor:ktor-server-core:${Versions.ktorVersion}")
    implementation("io.ktor:ktor-server-netty:${Versions.ktorVersion}")
//    runtime("ch.qos.logback:logback-classic:1.2.3")
    testImplementation( "io.ktor:ktor-server-test-host:${Versions.ktorVersion}")

}
