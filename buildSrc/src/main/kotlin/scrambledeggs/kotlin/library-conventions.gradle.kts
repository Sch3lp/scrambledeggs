package scrambledeggs.kotlin

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
}
