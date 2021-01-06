plugins {
    id("scrambledeggs.spring-conventions")
}

dependencies {
    implementation(project(":adapter:eventsourcing"))
    implementation("org.postgresql:postgresql:42.2.18")

    implementation(platform("org.jdbi:jdbi3-bom:3.18.0"))
    implementation("org.jdbi:jdbi3-core")
    implementation("org.jdbi:jdbi3-kotlin")
    implementation("org.jdbi:jdbi3-kotlin-sqlobject")
    implementation("org.jdbi:jdbi3-postgres")
}
