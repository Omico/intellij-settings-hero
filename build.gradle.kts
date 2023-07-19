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

repositories {
    mavenCentral()
}

group = "me.omico.intellij.settingsHero"
version = "0.1.0"

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(kotlinx.serialization.json)
}

intellij {
    pluginName.set("Settings Hero")
    version.set("2023.1.3")
    plugins.set(listOf("Git4Idea"))
}

changelog {
    groups.set(listOf("Added", "Changed", "Deprecated", "Removed", "Fixed", "Security"))
    repositoryUrl.set("https://github.com/Omico/intellij-settings-hero")
    version.set(project.version.toString())
    path.set(file("CHANGELOG.md").canonicalPath)
}

tasks {
    patchPluginXml {
        dependsOn(patchChangelog)
        sinceBuild.set("231")
        untilBuild.set("231.*")
        pluginDescription.set(
            providers.fileContents(layout.projectDirectory.file("README.md")).asText.map {
                val start = "<!-- Plugin description -->"
                val end = "<!-- Plugin description end -->"
                with(it.lines()) {
                    subList(indexOf(start) + 1, indexOf(end)).joinToString("\n").let(::markdownToHTML)
                }
            },
        )
        changeNotes.set(
            changelog.renderItem(
                item = run {
                    changelog
                        .getLatest()
                        .withHeader(false)
                        .withEmptySections(false)
                },
                outputType = Changelog.OutputType.HTML,
            ),
        )
    }
    spotlessFreshmark {
        dependsOn(patchChangelog)
    }
    runIde {
        autoReloadPlugins.set(true)
    }
}
