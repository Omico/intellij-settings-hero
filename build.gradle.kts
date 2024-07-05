@file:Suppress("UnstableApiUsage")

import me.omico.gradm.addDeclaredRepositories
import org.jetbrains.changelog.Changelog
import org.jetbrains.changelog.markdownToHTML

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("org.jetbrains.intellij")
    id("org.jetbrains.changelog")
    id("ijsh.root")
    id("ijsh.messages-properties-formatter")
}

addDeclaredRepositories()

kotlin {
    jvmToolchain(17)
}

configurations {
    runtimeClasspath {
        exclude(group = "org.jetbrains.kotlin")
    }
}

dependencies {
    implementation(kotlinx.serialization.json)
}

val targetIntelliJVersion = "2023.2.6"

intellij {
    pluginName.set("Settings Hero")
    version.set(targetIntelliJVersion)
    plugins.set(listOf("Git4Idea"))
}

changelog {
    repositoryUrl.set("https://github.com/Omico/intellij-settings-hero")
    version.set(project.version.toString())
    path.set(file("CHANGELOG.md").canonicalPath)
}

tasks {
    patchChangelog {
        onlyIf { !project.version.toString().endsWith("-SNAPSHOT") }
    }
    patchPluginXml {
        sinceBuild.set("232")
        untilBuild.set("241.*")
        pluginDescription.set(
            providers.fileContents(layout.projectDirectory.file("README.md")).asText.map {
                val start = "<!-- Plugin description -->"
                val end = "<!-- Plugin description end -->"
                with(it.lines()) {
                    subList(indexOf(start) + 1, indexOf(end)).joinToString("\n").let(::markdownToHTML)
                }
            },
        )
        changelog.getAll().values
            .filterNot(Changelog.Item::isUnreleased)
            .joinToString("\n") { item -> changelog.renderItem(item = item, outputType = Changelog.OutputType.HTML) }
            .let(changeNotes::set)
    }
    spotlessFreshmark {
        dependsOn(patchChangelog)
        doFirst {
            file("CHANGELOG.md").writeText(changelog.render())
        }
    }
    buildSearchableOptions {
        enabled = "runPluginVerifier" !in gradle.startParameter.taskNames
    }
    buildPlugin {
        archiveBaseName.set("SettingsHero")
    }
    runIde {
        autoReloadPlugins.set(true)
    }
    runPluginVerifier {
        ideVersions.set(listOf(targetIntelliJVersion, "2023.3.4", "2024.1"))
    }
}
