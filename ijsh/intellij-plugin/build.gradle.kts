@file:Suppress("UnstableApiUsage")

import org.jetbrains.changelog.Changelog
import org.jetbrains.changelog.markdownToHTML

plugins {
    id("ijsh.kotlin.jvm.base")
    id("ijsh.intellij.main")
    id("org.jetbrains.changelog")
    id("ijsh.messages-properties-formatter")
    kotlin("plugin.serialization")
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation(kotlinx.serialization.json)
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
        pluginDescription = providers.fileContents(layout.projectDirectory.file("README.md")).asText.map {
            val start = "<!-- Plugin description -->"
            val end = "<!-- Plugin description end -->"
            with(it.lines()) { subList(indexOf(start) + 1, indexOf(end)).joinToString("\n").let(::markdownToHTML) }
        }
        changeNotes = changelog.getAll().values
            .filterNot(Changelog.Item::isUnreleased)
            .joinToString("\n") { item -> changelog.renderItem(item = item, outputType = Changelog.OutputType.HTML) }
    }
    buildSearchableOptions {
        enabled = false
    }
    buildPlugin {
        archiveBaseName = "SettingsHero"
    }
    runIde {
        autoReload = true
    }
}
