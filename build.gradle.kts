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

val targetIntelliJVersion = "2024.1.4"

intellij {
    pluginName = "Settings Hero"
    version = targetIntelliJVersion
    plugins = listOf("Git4Idea")
}

changelog {
    repositoryUrl = "https://github.com/Omico/intellij-settings-hero"
    version = project.version.toString()
    path = file("CHANGELOG.md").canonicalPath
}

tasks {
    patchChangelog {
        onlyIf { !project.version.toString().endsWith("-SNAPSHOT") }
    }
    patchPluginXml {
        sinceBuild = "241"
        untilBuild = "242.*"
        pluginDescription = providers.fileContents(layout.projectDirectory.file("README.md")).asText.map {
            val start = "<!-- Plugin description -->"
            val end = "<!-- Plugin description end -->"
            with(it.lines()) { subList(indexOf(start) + 1, indexOf(end)).joinToString("\n").let(::markdownToHTML) }
        }
        changeNotes = changelog.getAll().values
            .filterNot(Changelog.Item::isUnreleased)
            .joinToString("\n") { item -> changelog.renderItem(item = item, outputType = Changelog.OutputType.HTML) }
    }
    spotlessFreshmark {
        dependsOn(patchChangelog)
        doFirst {
            file("CHANGELOG.md").writeText(changelog.render())
        }
    }
    buildSearchableOptions {
        enabled = false
    }
    buildPlugin {
        archiveBaseName = "SettingsHero"
    }
    runIde {
        autoReloadPlugins = true
    }
    runPluginVerifier {
        ideVersions = listOf(targetIntelliJVersion, "2024.2")
    }
}
