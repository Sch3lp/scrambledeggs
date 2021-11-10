plugins {
    id("scrambledeggs.kotlin.library")
    id("scrambledeggs.spring.di")
}

dependencies {
    api(project(":hex-matches:domain:api"))
    api(project(":infra"))
}