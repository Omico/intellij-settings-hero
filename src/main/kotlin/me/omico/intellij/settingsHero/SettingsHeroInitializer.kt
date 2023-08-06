@file:Suppress("UnstableApiUsage")

package me.omico.intellij.settingsHero

import com.intellij.ide.AppLifecycleListener
import com.intellij.ide.ApplicationInitializedListener
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.vcs.changes.ignore.cache.PatternCache
import kotlinx.coroutines.CoroutineScope
import me.omico.intellij.settingsHero.plugin.plugins
import me.omico.intellij.settingsHero.profile.SettingsHeroProfileManager
import me.omico.intellij.settingsHero.repository.localRepository
import me.omico.intellij.settingsHero.repository.refreshLocalRepository
import me.omico.intellij.settingsHero.utility.messageBus

lateinit var defaultProject: Project
    private set

lateinit var patternCache: PatternCache
    private set

internal class SettingsHeroInitializer : ApplicationInitializedListener {
    override suspend fun execute(asyncScope: CoroutineScope) {
        defaultProject = ProjectManager.getInstance().defaultProject
        patternCache = PatternCache.getInstance(defaultProject)
        if (!settingsHeroSettings.enabled) return
        if (settingsHeroSettings.localRepositoryDirectory.isBlank()) return
        refreshLocalRepository()
        SettingsHeroProfileManager.load()
        messageBus.connect().subscribe(
            AppLifecycleListener.TOPIC,
            object : AppLifecycleListener {
                override fun appClosing(): Unit = saveAll()
            },
        )
    }
}

internal fun saveAll() {
    if (!settingsHeroSettings.enabled) return
    val currentProfileName = settingsHeroSettings.currentProfile.ifBlank { return }
    localRepository.saveSettings(currentProfileName, patternCache)
    localRepository.savePlugins(currentProfileName, plugins)
}
