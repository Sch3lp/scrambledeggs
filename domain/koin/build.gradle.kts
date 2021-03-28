plugins {
    id("scrambledeggs.kotlin.library-conventions")
    id("scrambledeggs.koin.di-conventions")
}

dependencies {
    api(project(":domain:core"))
    api(project(":domain:core-api"))
    api(project(":domain:leaderboard"))
    api(project(":domain:leaderboard-api"))
    api(project(":infra"))
}