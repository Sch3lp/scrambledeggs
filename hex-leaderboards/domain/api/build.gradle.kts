plugins {
    id("scrambledeggs.kotlin.library-conventions")
}

dependencies {
    api(project(":infra"))
    api(project(":common:domain:api:error"))

}
