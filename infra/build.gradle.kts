plugins {
    id("scrambledeggs.kotlin.library-conventions")
    id("scrambledeggs.spring.di-conventions")
    id("scrambledeggs.spring.tx-conventions")
}

dependencies {
    implementation("org.hashids:hashids:1.0.3")
}