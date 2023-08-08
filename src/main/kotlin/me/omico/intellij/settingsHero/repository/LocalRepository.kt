// Copyright 2023 Omico
// SPDX-License-Identifier: GPL-3.0-only
package me.omico.intellij.settingsHero.repository

import com.intellij.openapi.vcs.changes.ignore.cache.PatternCache
import com.intellij.openapi.vcs.changes.ignore.lang.Syntax
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import me.omico.intellij.settingsHero.plugin.PluginInfo
import me.omico.intellij.settingsHero.profile.SettingsHeroProfileManager
import me.omico.intellij.settingsHero.profile.SettingsHeroProfiles
import me.omico.intellij.settingsHero.settingsHeroSettings
import me.omico.intellij.settingsHero.utility.clearDirectory
import me.omico.intellij.settingsHero.utility.ideaConfigurationDirectory
import me.omico.intellij.settingsHero.utility.prettyJson
import me.omico.intellij.settingsHero.utility.removeIdeaConfigurationDirectoryPrefix
import java.nio.file.Path
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.Path
import kotlin.io.path.copyTo
import kotlin.io.path.createDirectories
import kotlin.io.path.deleteRecursively
import kotlin.io.path.invariantSeparatorsPathString
import kotlin.io.path.readText
import kotlin.io.path.walk
import kotlin.io.path.writeText

data class LocalRepository(
    val root: Path,
) {
    private val profilesFile: Path = root.resolve("profiles.json")
    private val pluginsFile: Path = root.resolve("plugins.json")

    fun loadProfiles(
        initializer: (() -> SettingsHeroProfiles)? = null,
    ): SettingsHeroProfiles =
        runCatching { prettyJson.decodeFromString<SettingsHeroProfiles>(profilesFile.readText()) }
            .getOrElse { initializer?.invoke() ?: throw it }

    fun saveProfiles(profiles: SettingsHeroProfiles): Unit =
        prettyJson.encodeToString(profiles).let(profilesFile::writeText)

    fun profileDirectory(profileName: String): Path = root.resolve(profileName)

    fun loadPlugins(): Map<String, List<PluginInfo>> =
        runCatching { prettyJson.decodeFromString<Map<String, List<PluginInfo>>>(pluginsFile.readText()) }
            .getOrDefault(emptyMap())

    fun savePlugins(plugins: Map<String, List<PluginInfo>>): Unit =
        prettyJson.encodeToString(plugins).let(pluginsFile::writeText)

    fun savePlugins(profileName: String, profilePlugins: List<PluginInfo>) {
        val currentPlugins = loadPlugins().toMutableMap()
        currentPlugins[profileName] = profilePlugins
        savePlugins(currentPlugins)
    }

    fun saveSettings(
        profileName: String,
        patternCache: PatternCache,
    ) {
        val profile = SettingsHeroProfileManager.profiles.find { it.name == profileName } ?: return
        profileDirectory(profileName).saveSettings(patternCache, profile.rules)
    }

    @OptIn(ExperimentalPathApi::class)
    fun remove(profileName: String) {
        profileDirectory(profileName).deleteRecursively()
        val currentPlugins = loadPlugins().toMutableMap()
        currentPlugins.remove(profileName)
        savePlugins(currentPlugins)
    }
}

lateinit var localRepository: LocalRepository
    private set

private var previousLocalRepositoryDirectory: String? = null

fun refreshLocalRepository() {
    val currentLocalRepositoryDirectory = settingsHeroSettings.localRepositoryDirectory
    if (previousLocalRepositoryDirectory == currentLocalRepositoryDirectory) return
    localRepository = currentLocalRepositoryDirectory.let(::Path).let(::LocalRepository)
    previousLocalRepositoryDirectory = currentLocalRepositoryDirectory
}

@OptIn(ExperimentalPathApi::class)
fun Path.saveSettings(
    patternCache: PatternCache,
    rules: Set<String>,
) {
    clearDirectory()
    if (rules.isEmpty()) return
    val patterns = rules.mapNotNull { patternCache.createPattern(it, Syntax.GLOB) }
    val paths = ideaConfigurationDirectory.walk()
        .associateBy { it.invariantSeparatorsPathString.removeIdeaConfigurationDirectoryPrefix() }
    val filteredPaths = buildMap {
        patterns.forEach { pattern ->
            paths.filter { (pathString, _) -> pattern.matcher(pathString).matches() }.let(::putAll)
        }
    }
    filteredPaths.forEach { (pathString, path) ->
        val target = resolve(pathString)
        target.parent.createDirectories()
        path.copyTo(target)
    }
}
