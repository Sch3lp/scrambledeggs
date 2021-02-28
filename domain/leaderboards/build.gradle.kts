plugins {
    id("scrambledeggs.kotlin-library-conventions")
    id("scrambledeggs.spring-di-conventions")
}

dependencies {
    api(project(":domain:core-api"))
    api(project(":domain:leaderboards-api"))
    api(project(":infra"))

    //TODO: move InMemoryBroadcastEvents to proper "test-fixtures" config of leaderboards.api
    //see https://stackoverflow.com/questions/5644011/multi-project-test-dependencies-with-gradle/60138176#60138176
    //testImplementation(testFixtures(project(":domain:leaderboards-api")))

}