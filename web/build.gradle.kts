plugins {
    id("scrambledeggs.kotlin-conventions")
    id("scrambledeggs.spring-conventions")
}

dependencies {
    project(":adapter:rest-api")
    project(":adapter:eventsourcing")
    project(":adapter:rdbms")
    project(":domain:core")
    project(":domain:core-api")
    project(":infra")
    project(":ui")
}

tasks.withType<org.springframework.boot.gradle.tasks.bundling.BootJar> {
    baseName = "scrambledeggs-webapp"
}

tasks.processResources {
    dependsOn(":ui:build")
    from ("../ui/build/elm") {
        into("static")
    }
}