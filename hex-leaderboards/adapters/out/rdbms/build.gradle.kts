plugins {
    id("scrambledeggs.kotlin.library")
    id("scrambledeggs.spring.di")
    id("scrambledeggs.db.jdbi")
}

dependencies {
    api(project(":hex-leaderboards:domain:api"))

    testImplementation(project(":common:adapter:rdbms"))
}
