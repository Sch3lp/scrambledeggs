plugins {
    id("scrambledeggs.kotlin.library")
    id("scrambledeggs.spring.di")
    id("scrambledeggs.spring.tx")
}

dependencies {
    implementation("org.hashids:hashids:1.0.3")
}