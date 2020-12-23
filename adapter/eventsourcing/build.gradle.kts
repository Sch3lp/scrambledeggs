plugins {
    id("scrambledeggs.spring-conventions")
}

dependencies {
    implementation("org.springframework.data:spring-data-r2dbc")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("io.r2dbc:r2dbc-spi")
    implementation("io.r2dbc:r2dbc-postgresql")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
}