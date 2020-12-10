plugins {
    id("org.mohme.gradle.elm-plugin" ) version "4.0.1"
}

elm {
    sourceDir.set(project.file("src/main/elm"))
    targetModuleName.set("main.js")
    val exDir = project.projectDir.path // necessary for running this task outside of this project (because defaults to ".")
    executionDir.set(exDir)
    debug.set(true)
    optimize.set(false)
}

tasks.register("build") {
    dependsOn("elmMake")
}