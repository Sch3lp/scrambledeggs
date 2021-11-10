plugins {
    id("scrambledeggs.kotlin.library-conventions")
    id("scrambledeggs.spring.di-conventions")
}

dependencies {
    api(project(":hex-leaderboards:domain:api"))

    api(platform("org.jdbi:jdbi3-bom:3.18.0"))
    api("org.jdbi:jdbi3-core")
    api("org.jdbi:jdbi3-kotlin")
    api("org.jdbi:jdbi3-kotlin-sqlobject")
    api("org.jdbi:jdbi3-postgres")
    api("org.jdbi:jdbi3-spring4")
    testImplementation(project(":common:adapter:rdbms"))
}
