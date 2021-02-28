plugins {
    id("scrambledeggs.kotlin-library-conventions")
    id("scrambledeggs.spring-conventions")
}

dependencies {
    api(project(":infra"))
    api(project(":domain:core-api"))
    api(project(":domain:leaderboards-api"))
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}