plugins {
    id("scrambledeggs.kotlin.library-conventions")
}

dependencies {
    api(project(":infra"))
    testImplementation(testFixtures(project(":infra")))
}
