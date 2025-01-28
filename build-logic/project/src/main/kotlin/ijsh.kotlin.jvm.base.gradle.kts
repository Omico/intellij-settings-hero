import org.gradle.kotlin.dsl.kotlin

plugins {
    kotlin("jvm")
    id("me.omico.consensus.spotless")
}

consensus {
    spotless {
        kotlin(
            licenseHeaderFile = rootProject.file("spotless/copyright.kt"),
        )
        kotlinGradle()
    }
}
