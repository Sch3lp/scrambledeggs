plugins {
    id("scrambledeggs.spring-conventions")
}

dependencies {
    implementation(project(":adapter:eventsourcing"))
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}