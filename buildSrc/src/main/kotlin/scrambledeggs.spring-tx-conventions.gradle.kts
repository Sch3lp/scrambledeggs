plugins {
    id("scrambledeggs.kotlin-common-conventions")
    id("scrambledeggs.spring-common-conventions")
}

dependencies {
    val springTxVersion = dependencyManagement.managedVersions["org.springframework:spring-tx"]
    implementation("org.springframework:spring-tx:$springTxVersion")
}
