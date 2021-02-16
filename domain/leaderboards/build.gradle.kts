plugins {
    id("scrambledeggs.spring-conventions")
}

dependencies {
    implementation(project(":domain:core-api"))
    implementation(project(":domain:leaderboards-api"))
    implementation(project(":infra"))

    //TODO: move InMemoryBroadcastEvents to proper "test-fixtures" config of leaderboards.api
    //see https://stackoverflow.com/questions/5644011/multi-project-test-dependencies-with-gradle/60138176#60138176
    //testImplementation(testFixtures(project(":domain:leaderboards-api")))

//    //TODO separate scrambledeggs.spring-conventions into spring-di and others, so we don't need to hardcode a version and use the BOM instead
//    implementation("org.springframework:spring-context:5.3.1")
//
//    //TODO separate scrambledeggs.spring-conventions into impl and test conventions?
//    testImplementation("org.springframework.boot:spring-boot-starter-web:2.4.0")
//    testImplementation("org.springframework.boot:spring-boot-starter-actuator:2.4.0")
//    testImplementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.11.3")
}