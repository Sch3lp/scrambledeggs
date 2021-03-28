plugins {
    id("scrambledeggs.kotlin.library-conventions")
    id("scrambledeggs.ktor.web-conventions")
    id("scrambledeggs.koin.di-conventions")
}

dependencies {
    api(project(":infra"))
    api(project(":domain:core-api"))
    api(project(":domain:leaderboards-api"))
}