// Copyright 2023 Omico
// SPDX-License-Identifier: GPL-3.0-only
package me.omico.intellij.settingsHero.profile

import me.omico.intellij.settingsHero.settingsHeroSettings
import java.nio.file.Path
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.deleteRecursively

object SettingsHeroProfileManager {
    private lateinit var repositoryDirectory: Path
    private var persistentProfiles: SettingsHeroProfiles = emptyList()
    private var temporaryProfiles: SettingsHeroProfiles = emptyList()

    val profiles: SettingsHeroProfiles
        get() = temporaryProfiles

    fun initialize(repositoryDirectory: Path) {
        SettingsHeroProfileManager.repositoryDirectory = repositoryDirectory
        load(repositoryDirectory)
    }

    fun find(name: String): SettingsHeroProfile? = temporaryProfiles.find { it.name == name }

    fun save() {
        settingsHeroSettings.currentProfile?.let(::find)?.saveTo(repositoryDirectory)
        val names = temporaryProfiles.names
        persistentProfiles
            .filterNot { it.name in names }
            .forEach { profile ->
                @OptIn(ExperimentalPathApi::class)
                repositoryDirectory.resolve(profile.name).deleteRecursively()
            }
        persistentProfiles = temporaryProfiles
    }

    fun reset() {
        temporaryProfiles = persistentProfiles
    }

    fun new(name: String = "New Profile"): SettingsHeroProfile {
        val newProfile = newSettingsHeroProfile(findAvailableNewName(name))
        modify { this + newProfile }
        return newProfile
    }

    fun remove(name: String): Unit = modify { filterNot { it.name == name } }

    fun rename(oldName: String, newName: String): Unit = modify(oldName) { copy(name = newName) }

    fun replace(newProfile: SettingsHeroProfile): Unit = modify(newProfile.name) { newProfile }

    fun duplicate(name: String) {
        val profile = find(name) ?: return
        val newProfileName = findAvailableNewName("${profile.name} (Copy)")
        modify { this + profile.copy(name = newProfileName) }
    }

    fun isModified(): Boolean = persistentProfiles != temporaryProfiles

    private fun load(repositoryDirectory: Path) {
        persistentProfiles = loadSettingsHeroProfiles(repositoryDirectory)
        temporaryProfiles = persistentProfiles
    }

    private fun findAvailableNewName(name: String): String {
        val names = profiles.names
        if (name !in names) return name
        val count = names
            .asSequence()
            .filter { it.startsWith(name) }
            .map { it.removePrefix(name).ifEmpty { "1" }.trim() }
            .mapNotNull(String::toIntOrNull)
            .maxOrNull() ?: 0
        val newProfileName = when (count) {
            0 -> name
            else -> "$name (New)"
        }
        return newProfileName
    }

    private fun modify(modifier: SettingsHeroProfiles.() -> SettingsHeroProfiles) {
        temporaryProfiles = temporaryProfiles.modifier()
    }

    private fun modify(name: String, modifier: SettingsHeroProfile.() -> SettingsHeroProfile): Unit =
        modify {
            map { profile ->
                when (profile.name) {
                    name -> profile.modifier()
                    else -> profile
                }
            }
        }
}
