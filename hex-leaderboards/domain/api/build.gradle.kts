plugins {
    id("scrambledeggs.kotlin.library")
}

dependencies {
    api(project(":infra"))
    api(project(":common:domain:api:error"))

}
