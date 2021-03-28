package scrambledeggs.ktor

import gradle.kotlin.dsl.accessors._1fedb3f582a8ee457b78c12de5a29dae.api
import gradle.kotlin.dsl.accessors._1fedb3f582a8ee457b78c12de5a29dae.implementation
import org.gradle.kotlin.dsl.`java-library`
import org.gradle.kotlin.dsl.`java-test-fixtures`
import org.gradle.kotlin.dsl.dependencies

plugins {
    id("scrambledeggs.kotlin.application-conventions")
    id("scrambledeggs.ktor.common-conventions")
    id("com.github.johnrengelman.shadow")
}

dependencies {

    api("org.slf4j:slf4j-api:1.7.30")

}

//
//application {
//    mainClass.set("io.ktor.server.netty.EngineMain")
//}
//
//tasks.withType<Jar> {
//    manifest {
//        attributes(
//            mapOf(
//                "Main-Class" to application.mainClass
//            )
//        )
//    }
//}