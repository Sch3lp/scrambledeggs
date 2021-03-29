plugins {
    id("scrambledeggs.kotlin.application-conventions")
    id("scrambledeggs.ktor.server-conventions")
    id("scrambledeggs.koin.di-conventions")
}

dependencies {
    api(project(":adapter:ktor-api"))
//    api(project(":adapter:eventsourcing")) //todo dupe and replace spring deps with koin
    api(project(":adapter:rdbms"))
    api(project(":domain:core"))
    api(project(":domain:core-api"))
    api(project(":domain:leaderboards"))
    api(project(":domain:leaderboards-api"))
//    api(project(":infra"))
}

tasks.processResources {
    dependsOn(":ui:build")
    from ("../ui/build/elm") {
        into("static")
    }
}

application {
    mainClass.set("io.ktor.server.netty.EngineMain")
}

tasks.withType<Jar> {
    manifest {
        attributes(
            mapOf(
                "Main-Class" to application.mainClass
            )
        )
    }
}