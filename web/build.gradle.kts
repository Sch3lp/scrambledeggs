plugins {
    id("scrambledeggs.kotlin.application")
    id("scrambledeggs.spring.boot")
}

dependencies {
    api(project(":common:adapter:rest-api"))
    api(project(":common:adapter:rdbms"))

    api(project(":common:domain:api:error"))

    api(project(":hex-matches:domain:core"))
    api(project(":hex-matches:domain:api"))
    api(project(":hex-matches:adapters:in:rest"))
    api(project(":hex-matches:adapters:out:rdbms"))

    api(project(":hex-leaderboards:domain:core"))
    api(project(":hex-leaderboards:domain:api"))
    api(project(":hex-leaderboards:adapters:in:rest"))
    api(project(":hex-leaderboards:adapters:out:eventsourcing"))
    api(project(":hex-leaderboards:adapters:out:rdbms"))

    api(project(":infra"))

    val ktorVersion = "1.5.1"
    testImplementation("io.ktor:ktor-client-cio:$ktorVersion")
    testImplementation("io.ktor:ktor-client-jackson:$ktorVersion")
    testImplementation("io.ktor:ktor-client-logging:$ktorVersion")
    testImplementation("org.springframework.data:spring-data-r2dbc")

    testImplementation(project(":common:adapter:rdbms"))
    testImplementation(testFixtures(project(":common:adapter:rest-api")))
}

tasks.withType<org.springframework.boot.gradle.tasks.bundling.BootJar> {
    archiveBaseName.set("scrambledeggs-webapp")
    mainClass.set("org.scrambled.ScrambledApplication")
}

tasks.processResources {
    dependsOn(":ui:build")
    from ("../ui/build/elm") {
        into("static")
    }
}

/*
Before, there was javac
Then, there was ANT
Then, there was Ivy, Ivy introduced conventional structure, it also introduced dependency management
Then, there was Maven, who used the trend of the day and made everything look like XML
Then, Maven evolved to Maven 2, and Maven Central (or maven repositories) sprouted out of the ground everywhere
Then, there was Gradle, who got rid of XML notation in favor of actual (script) code, with performance improvements (like proper caching, etc.)
Then, Gradle improved with kotlin script (instead of Groovy Script)
 */