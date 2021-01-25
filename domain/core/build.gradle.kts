plugins {
    id("scrambledeggs.kotlin-conventions")
}

dependencies {
    implementation(project(":domain:core-api"))
    implementation(project(":infra"))
}