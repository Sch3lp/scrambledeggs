package scrambledeggs.ktor

import gradle.kotlin.dsl.accessors._1fedb3f582a8ee457b78c12de5a29dae.api
import org.gradle.kotlin.dsl.`java-library`
import org.gradle.kotlin.dsl.`java-test-fixtures`
import org.gradle.kotlin.dsl.dependencies

plugins {
    id("scrambledeggs.kotlin-common-conventions")
    id("scrambledeggs.ktor.common-conventions")
    `java-library`
    `java-test-fixtures`
}

dependencies {

    api("org.slf4j:slf4j-api:1.7.30")


}
