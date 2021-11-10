plugins {
    id("scrambledeggs.kotlin.library")
    id("scrambledeggs.spring.web")
    id("scrambledeggs.spring.security")
}

dependencies {
    api(project(":infra"))
    api(project(":hex-matches:domain:api"))
    api(project(":common:adapter:rest-api"))
}