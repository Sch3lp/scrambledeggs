package scrambledeggs.kotlin

import gradle.kotlin.dsl.accessors._94658c77fd8a61767179a858a62d95a6.implementation
import gradle.kotlin.dsl.accessors._94658c77fd8a61767179a858a62d95a6.java
import gradle.kotlin.dsl.accessors._94658c77fd8a61767179a858a62d95a6.test
import gradle.kotlin.dsl.accessors._94658c77fd8a61767179a858a62d95a6.testImplementation
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.repositories
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.jetbrains.kotlin.jvm")
}

java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenCentral()
}

dependencies {
    constraints {
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    }

    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation(platform("org.jetbrains.kotlinx:kotlinx-coroutines-bom:1.4.2")) //dep is not tied to kotlin version because it's experimental


    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")

    testImplementation("org.junit.jupiter:junit-jupiter:5.6.2")
    testImplementation("org.assertj:assertj-core:3.14.0")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        languageVersion = "1.4"
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}