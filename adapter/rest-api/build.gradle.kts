plugins {
    id("scrambledeggs.spring-conventions")
}

dependencies {
    implementation(project(":adapter:eventsourcing"))
    implementation(project(":infra"))
    implementation(project(":domain:core-api"))
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}