plugins {
    id("scrambledeggs.kotlin-conventions")
}

dependencies {
    implementation(project(":domain:core-api"))
    implementation(project(":infra"))
    //TODO separate scrambledeggs.spring-conventions into spring-di and others, so we don't need to hardcode a version and use the BOM instead
    implementation("org.springframework:spring-context:5.3.1")
}