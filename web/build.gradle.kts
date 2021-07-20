plugins {
    id("scrambledeggs.kotlin.application-conventions")
    id("scrambledeggs.spring.boot-conventions")
}

dependencies {
    api(project(":adapter:rest-api"))
    api(project(":adapter:eventsourcing"))
    api(project(":adapter:rdbms"))
    api(project(":domain:core"))
    api(project(":domain:core-api"))
    api(project(":domain:leaderboards"))
    api(project(":domain:leaderboards-api"))
    api(project(":infra"))

    val ktorVersion = "1.5.1"
    testImplementation("io.ktor:ktor-client-cio:$ktorVersion")
    testImplementation("io.ktor:ktor-client-jackson:$ktorVersion")
    testImplementation("io.ktor:ktor-client-logging:$ktorVersion")
    testImplementation(platform("org.jdbi:jdbi3-bom:3.18.0"))
    testImplementation("org.jdbi:jdbi3-core")
    testImplementation("org.springframework.data:spring-data-r2dbc")

    testImplementation(testFixtures(project(":adapter:rest-api")))
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