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
}

tasks.processResources {
    dependsOn(":ui:build")
    from ("../ui/build/elm") {
        into("static")
    }
}