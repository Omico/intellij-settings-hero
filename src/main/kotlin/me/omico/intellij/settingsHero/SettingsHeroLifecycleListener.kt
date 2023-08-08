// Copyright 2023 Omico
// SPDX-License-Identifier: GPL-3.0-only
@file:Suppress("UnstableApiUsage")

package me.omico.intellij.settingsHero

import com.intellij.ide.AppLifecycleListener
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.vcs.changes.ignore.cache.PatternCache
import me.omico.intellij.settingsHero.plugin.plugins
import me.omico.intellij.settingsHero.profile.SettingsHeroProfileManager
import me.omico.intellij.settingsHero.repository.localRepository
import me.omico.intellij.settingsHero.repository.refreshLocalRepository

internal class SettingsHeroLifecycleListener : AppLifecycleListener {
    override fun appFrameCreated(commandLineArgs: MutableList<String>) {
        defaultProject = ProjectManager.getInstance().defaultProject
        patternCache = PatternCache.getInstance(defaultProject)
        if (!settingsHeroSettings.enabled) return
        if (settingsHeroSettings.localRepositoryDirectory.isBlank()) return
        refreshLocalRepository()
        SettingsHeroProfileManager.load()
    }

    override fun appClosing(): Unit = saveAll()
}

internal lateinit var defaultProject: Project
    private set

internal lateinit var patternCache: PatternCache
    private set

internal fun saveAll() {
    if (!settingsHeroSettings.enabled) return
    val currentProfileName = settingsHeroSettings.currentProfile.ifBlank { return }
    localRepository.saveSettings(currentProfileName, patternCache)
    localRepository.savePlugins(currentProfileName, plugins)
}
