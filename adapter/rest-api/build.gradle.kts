plugins {
    id("scrambledeggs.kotlin-library-conventions")
    id("scrambledeggs.spring-boot-conventions")
}

dependencies {
    api(project(":infra"))
    api(project(":domain:core-api"))
    api(project(":domain:leaderboards-api"))
}