plugins {
    id("scrambledeggs.kotlin-library-conventions")
    id("scrambledeggs.spring-di-conventions")
}

dependencies {
    api(project(":domain:core-api"))
    api(project(":domain:leaderboards-api"))
    api(project(":infra"))

    testImplementation(testFixtures(project(":domain:leaderboards-api")))
}