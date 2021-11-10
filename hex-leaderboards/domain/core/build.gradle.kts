plugins {
    id("scrambledeggs.kotlin.library-conventions")
    id("scrambledeggs.spring.di-conventions")
}

dependencies {
    api(project(":hex-matches:domain:api"))
    api(project(":hex-leaderboards:domain:api"))
    api(project(":infra"))

    testImplementation(testFixtures(project(":hex-leaderboards:domain:api")))
}