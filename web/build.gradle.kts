plugins {
    id("scrambledeggs.kotlin-conventions")
    id("scrambledeggs.spring-conventions")
}

dependencies {
    implementation(project(":adapter:rest-api"))
    implementation(project(":adapter:eventsourcing"))
    implementation(project(":adapter:rdbms"))
    implementation(project(":domain:core"))
    implementation(project(":domain:core-api"))
    implementation(project(":infra"))
}

tasks.withType<org.springframework.boot.gradle.tasks.bundling.BootJar> {
    baseName = "scrambledeggs-webapp"
    mainClassName = "org.scrambled.ScrambledApplication"
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