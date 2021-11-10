plugins {
    id("scrambledeggs.kotlin.library")
}

dependencies {
    api(project(":infra"))
    api(project(":common:domain:api:error"))
    api(project(":common:domain:api:security"))
    testImplementation(testFixtures(project(":infra")))
}
