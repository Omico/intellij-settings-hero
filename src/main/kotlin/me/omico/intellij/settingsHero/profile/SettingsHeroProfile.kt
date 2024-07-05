// Copyright 2023 Omico
// SPDX-License-Identifier: GPL-3.0-only
package me.omico.intellij.settingsHero.profile

import me.omico.intellij.settingsHero.plugin.SettingsHeroPluginManager
import me.omico.intellij.settingsHero.utility.clearDirectory
import me.omico.intellij.settingsHero.utility.ideaConfigurationDirectory
import me.omico.intellij.settingsHero.utility.removeIdeaConfigurationDirectoryPrefix
import java.nio.file.FileSystems
import java.nio.file.Path
import kotlin.io.path.copyTo
import kotlin.io.path.createDirectories
import kotlin.io.path.createFile
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
        "*.db",
        "*.kdbx",
        "*.key",
        "*.license",
        "*.pwd",
        "*.txt",
        "*.vmoptions",
        ".DS_Store",
        ".lock",
        ".updated_plugins_list",
        "codestyles/Default.xml",
        "event-log-metadata",
        "extensions",
        "inspection",
        "jdbc-drivers",
        "migration",
        "options/*.local.xml",
        "options/AquaNewUserFeedbackService.xml",
        "options/AquaOldUserFeedbackService.xml",
        "options/NewUIInfoService.xml",
        "options/actionSummary.xml",
        "options/features.usage.statistics.xml",
        "options/jdk.table.xml",
        "options/other.xml",
        "options/overrideFileTypes.xml",
        "options/path.macros.xml",
        "options/project.default.xml",
        "options/recentProjects.xml",
        "options/runner.layout.xml",
        "options/settingsSync.xml",
        "options/sshRecentConnections*.xml",
        "options/terminal.xml",
        "options/trusted-paths.xml",
        "options/updates.xml",
        "options/vcs-inputs.xml",
        "options/window.state.xml",
        "plugins",
        "scratches",
        "settingsSync",
        "ssl",
        "tasks",
        "workspace",
    )

internal fun Path.saveSettings(rules: Set<String>) {
    runCatching(Path::clearDirectory)
    if (rules.isEmpty()) return
    val filteredPaths = mutableMapOf<String, Path>()
    filterPaths(filteredPaths, rules, ideaConfigurationDirectory)
    filteredPaths.toSortedMap().forEach { (pathString, path) ->
        val target = resolve(pathString)
        target.parent.createDirectories()
        path.copyTo(target)
    }
}

private val allowedDirectories: Set<String> =
    setOf(
        "codestyles",
        "colors",
        "keymaps",
        "options",
    )

private fun filterPaths(filteredPaths: MutableMap<String, Path>, rules: Set<String>, path: Path): Unit =
    path.listDirectoryEntries()
        .associateBy { it.invariantSeparatorsPathString.removeIdeaConfigurationDirectoryPrefix() }
        .filterNot { (pathString, path) ->
            val relativePath = ideaConfigurationDirectory.relativize(path)
            if (allowedDirectories.any(relativePath::startsWith)) return@filterNot false
            rules.any { rule ->
                when {
                    '*' in rule -> FileSystems.getDefault().getPathMatcher("glob:$rule").matches(relativePath)
                    else -> pathString.startsWith(rule)
                }
            }
        }
        .onEach { (_, path) -> if (path.isDirectory()) filterPaths(filteredPaths, rules, path) }
        .let(filteredPaths::putAll)

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
