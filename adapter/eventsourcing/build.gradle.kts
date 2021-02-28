plugins {
    id("scrambledeggs.kotlin-library-conventions")
    id("scrambledeggs.spring-conventions")
}

dependencies {
    api(project(":domain:leaderboards-api"))

    implementation("org.springframework.data:spring-data-r2dbc")
    implementation("io.r2dbc:r2dbc-spi")
    implementation("io.r2dbc:r2dbc-postgresql")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    testImplementation("org.testcontainers:testcontainers:1.15.1")
    testImplementation("org.testcontainers:postgresql:1.15.1")
    testImplementation("org.postgresql:postgresql:42.2.18")
    testImplementation("org.testcontainers:junit-jupiter:1.15.1")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-test-autoconfigure")
}