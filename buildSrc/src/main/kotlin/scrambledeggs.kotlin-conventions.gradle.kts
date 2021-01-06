import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.jetbrains.kotlin.jvm")
    `java-library`
}

java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation(platform("org.jetbrains.kotlinx:kotlinx-coroutines-bom:1.4.2")) //TODO figure out why I need to supply a specific version here


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