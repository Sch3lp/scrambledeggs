plugins {
    id("scrambledeggs.kotlin.library-conventions")
    id("scrambledeggs.spring.web-conventions")
    id("scrambledeggs.spring.security-conventions")
}

dependencies {
    api(project(":infra"))
    api(project(":hex-matches:domain:api"))
    api(project(":common:adapter:rest-api"))
}