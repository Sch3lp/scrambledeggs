plugins {
    id("scrambledeggs.kotlin.library")
    id("scrambledeggs.spring.di")
    id("scrambledeggs.db.jdbi")
}

dependencies {
    api(project(":hex-matches:domain:api"))

    testImplementation(project(":common:adapter:rdbms"))
}
