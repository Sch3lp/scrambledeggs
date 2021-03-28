plugins {
    id("scrambledeggs.kotlin.library-conventions")
    id("scrambledeggs.spring.di-conventions")
}

dependencies {
    api(project(":domain:core-api"))
    api(project(":infra"))
}