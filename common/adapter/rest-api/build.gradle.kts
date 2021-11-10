plugins {
    id("scrambledeggs.kotlin.library-conventions")
    id("scrambledeggs.spring.web-conventions")
    id("scrambledeggs.spring.security-conventions")
}

dependencies {
    api(project(":infra"))
    api(project(":common:domain:api:error"))
    api(project(":common:domain:api:security"))
}