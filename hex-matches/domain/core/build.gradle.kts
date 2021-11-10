plugins {
    id("scrambledeggs.kotlin.library-conventions")
    id("scrambledeggs.spring.di-conventions")
}

dependencies {
    api(project(":hex-matches:domain:api"))
    api(project(":infra"))
}