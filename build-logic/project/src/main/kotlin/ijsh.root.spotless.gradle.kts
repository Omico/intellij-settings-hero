import me.omico.consensus.spotless.ConsensusSpotlessTokens

plugins {
    id("me.omico.consensus.spotless")
}

consensus {
    spotless {
        freshmark()
        gradleProperties()
        intelliJIDEARunConfiguration()
        kotlin(
            targets = ConsensusSpotlessTokens.Kotlin.targets + setOf(
                "build-logic/*/src/**/*.kt",
            ),
            licenseHeaderFile = rootProject.file("spotless/copyright.kt"),
        )
        kotlinGradle(
            targets = ConsensusSpotlessTokens.KotlinGradle.targets + setOf(
                "build-logic/*/src/**/*.gradle.kts",
            ),
        )
    }
}

subprojects {
    rootProject.tasks {
        spotlessApply { this@subprojects.tasks.findByName("spotlessApply")?.dependsOn(this) }
        spotlessCheck { this@subprojects.tasks.findByName("spotlessApply")?.dependsOn(this) }
    }
}
