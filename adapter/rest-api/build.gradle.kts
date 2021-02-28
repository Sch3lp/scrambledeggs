plugins {
    id("scrambledeggs.kotlin-library-conventions")
    id("scrambledeggs.spring-conventions")
}

dependencies {
    implementation(project(":infra"))
    implementation(project(":domain:core-api"))
    implementation(project(":domain:leaderboards-api"))
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}