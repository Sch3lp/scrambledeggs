plugins {
    id("scrambledeggs.kotlin.library-conventions")
    id("scrambledeggs.spring.web-conventions")
    id("scrambledeggs.spring.security-conventions")
}

dependencies {
    api(project(":infra"))
    api(project(":domain:core-api"))
    api(project(":domain:leaderboards-api"))
}