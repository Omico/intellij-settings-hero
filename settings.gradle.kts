rootProject.name = "intellij-settings-hero"

pluginManagement {
    includeBuild("build-logic/gradm")
}

plugins {
    id("ijsh.gradm")
}

includeBuild("build-logic/project")
