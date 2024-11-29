import org.jetbrains.intellij.platform.gradle.extensions.intellijPlatform

plugins {
    id("org.jetbrains.intellij.platform.settings")
}

dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    repositories {
        intellijPlatform {
            defaultRepositories()
        }
    }
}
