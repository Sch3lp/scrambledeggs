plugins {
    id("scrambledeggs.kotlin.library")
    id("scrambledeggs.spring.web")
    id("scrambledeggs.spring.security")
}

dependencies {
    api(project(":common:domain:api:error"))
    api(project(":common:domain:api:security"))
}