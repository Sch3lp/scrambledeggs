plugins {
    id("org.mohme.gradle.elm-plugin" ) version "4.0.1"
}

elm {
    sourceDir.set(project.file("src/main/elm"))
    targetModuleName.set("main.js")
    debug.set(true)
    optimize.set(false)
}