import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.jetbrains.kotlin.jvm")
    `java-library`
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    testImplementation("org.junit.jupiter:junit-jupiter:5.6.2")
    testImplementation("org.assertj:assertj-core:3.14.0")
}

tasks.test {
    useJUnitPlatform()
}

val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    languageVersion = "1.4"
}