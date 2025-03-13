// Copyright 2023-2025 Omico
// SPDX-License-Identifier: GPL-3.0-only
package me.omico.intellij.settingsHero.profile

import com.intellij.configurationStore.getExportableComponentsMap
import com.intellij.configurationStore.getExportableItemsFromLocalStorage
import com.intellij.util.io.createParentDirectories
import me.omico.intellij.settingsHero.plugin.SettingsHeroPluginManager
import me.omico.intellij.settingsHero.utility.clearDirectory
import me.omico.intellij.settingsHero.utility.ideaConfigurationDirectory
import me.omico.intellij.settingsHero.utility.storageManager
import java.nio.file.FileSystems
import java.nio.file.Path
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.copyTo
import kotlin.io.path.copyToRecursively
import kotlin.io.path.createDirectories
import kotlin.io.path.createFile
import kotlin.io.path.deleteIfExists
import kotlin.io.path.exists
import kotlin.io.path.invariantSeparatorsPathString
import kotlin.io.path.isDirectory
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.name
import kotlin.io.path.notExists
import kotlin.io.path.readLines
import kotlin.io.path.writeLines

data class SettingsHeroProfile(
    val name: String,
    val settingsGitIgnoreRules: Set<String>,
)

typealias SettingsHeroProfiles = List<SettingsHeroProfile>

val SettingsHeroProfiles.names: List<String>
    get() = map(SettingsHeroProfile::name)

internal fun loadSettingsHeroProfiles(repositoryDirectory: Path): SettingsHeroProfiles =
    repositoryDirectory.listDirectoryEntries()
        .filter(Path::isDirectory)
        .filter { it.resolve(SettingsHeroProfileConstants.DOT_FILE_NAME).exists() }
        .map(Path::loadAsSettingsHeroProfile)
        .sortedBy(SettingsHeroProfile::name)

internal fun SettingsHeroProfile.saveTo(repositoryDirectory: Path) {
    val profileRootDirectory = repositoryDirectory.resolve(name).createDirectories()
    val dotFile = profileRootDirectory.resolve(SettingsHeroProfileConstants.DOT_FILE_NAME)
    val settingsDirectory =
        profileRootDirectory.resolve(SettingsHeroProfileConstants.SETTINGS_DIRECTORY_NAME).createDirectories()
    val settingsGitIgnoreFile = settingsDirectory.resolve(SettingsHeroProfileConstants.SETTINGS_GIT_IGNORE_FILE_NAME)
    if (dotFile.notExists()) dotFile.createFile()
    SettingsHeroPluginManager.save(profileRootDirectory)
    settingsDirectory.saveSettings(settingsGitIgnoreRules)
    settingsGitIgnoreFile.writeLines(settingsGitIgnoreRules.toSortedSet())
}

internal fun newSettingsHeroProfile(name: String): SettingsHeroProfile =
    SettingsHeroProfile(
        name = name,
        settingsGitIgnoreRules = DefaultRules,
    )

internal val DefaultRules: Set<String> =
    setOf(
        "codestyles/Default.xml",
        "options/DontShowAgainFeedbackService.xml",
        "options/github.xml",
        "options/jdk.table.xml",
        "options/jdk.table.xml",
        "options/k2-feedback.xml",
        "options/kotlin-onboarding.xml",
        "options/kotlin-wizard-data.xml",
    )

@OptIn(ExperimentalPathApi::class)
@Suppress("UnstableApiUsage")
internal fun Path.saveSettings(rules: Set<String>) {
    runCatching(Path::clearDirectory)
    if (rules.isEmpty()) return
    val storageManager = storageManager()
    val exportableComponentsMap = getExportableComponentsMap(
        isComputePresentableNames = true,
        storageManager = storageManager,
        withExportable = false,
    )
    val exportableItems = getExportableItemsFromLocalStorage(
        exportableItems = exportableComponentsMap,
        storageManager = storageManager,
    )
    exportableItems.keys.forEach { path ->
        val relativizedPath = ideaConfigurationDirectory.relativize(path)
        val outputPath = resolve(relativizedPath).createParentDirectories()
        when {
            path.isDirectory() -> path.copyToRecursively(outputPath, followLinks = false)
            else -> path.copyTo(outputPath)
        }
    }
    val filteredPaths = mutableSetOf<Path>()
    listDirectoryEntries().forEach { path -> filterPaths(filteredPaths, rules, this, path) }
    filteredPaths.forEach(Path::deleteIfExists)
}

@OptIn(ExperimentalPathApi::class)
private fun filterPaths(filteredPaths: MutableSet<Path>, rules: Set<String>, root: Path, sub: Path) {
    if (sub.isDirectory()) {
        sub.listDirectoryEntries().forEach { filterPaths(filteredPaths, rules, root, it) }
    } else {
        val relativePath = root.relativize(sub)
        val matched = rules.any { rule ->
            when {
                '*' in rule -> FileSystems.getDefault().getPathMatcher("glob:$rule").matches(relativePath)
                else -> relativePath.invariantSeparatorsPathString == rule
            }
        }
        if (matched) filteredPaths.add(sub)
    }
}

private fun Path.loadAsSettingsHeroProfile(): SettingsHeroProfile =
    SettingsHeroProfile(
        name = name,
        settingsGitIgnoreRules = run {
            val file = this@loadAsSettingsHeroProfile
                .resolve(SettingsHeroProfileConstants.SETTINGS_DIRECTORY_NAME)
                .resolve(SettingsHeroProfileConstants.SETTINGS_GIT_IGNORE_FILE_NAME)
            when {
                !file.exists() -> DefaultRules
                else -> file.readLines().toSortedSet()
            }
        },
    )
