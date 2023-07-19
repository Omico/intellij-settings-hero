package me.omico.intellij.settingsHero.profile

import com.intellij.util.io.createDirectories
import me.omico.intellij.settingsHero.repository.localRepository
import me.omico.intellij.settingsHero.utility.clearAndAddAll

object SettingsHeroProfileManager {
    private val persistenceProfiles = mutableListOf<SettingsHeroProfile>()
    private val temporaryProfiles = mutableListOf<SettingsHeroProfile>()

    val profiles: SettingsHeroProfiles
        get() = temporaryProfiles

    fun load() {
        val profiles = localRepository.loadProfiles(::prepareInitialData)
        persistenceProfiles.clearAndAddAll(profiles)
        temporaryProfiles.clearAndAddAll(profiles)
    }

    fun temporaryProfile(profileName: String): SettingsHeroProfile =
        temporaryProfiles.firstOrNull { it.name == profileName } ?: SettingsHeroProfile.Empty

    fun newTemporaryProfile(name: String = "New Profile"): SettingsHeroProfile {
        val newProfileName = findAvailableNewName(name)
        val newProfile = SettingsHeroProfile(newProfileName)
        temporaryProfiles.add(newProfile)
        return newProfile
    }

    fun removeTemporaryProfile(profileName: String) {
        temporaryProfiles.removeIf { it.name == profileName }
        if (temporaryProfiles.isEmpty()) {
            temporaryProfiles.add(SettingsHeroProfile.Default)
        }
    }

    fun renameTemporaryProfile(oldProfileName: String, newProfileName: String) {
        temporaryProfiles.replaceAll {
            when (it.name) {
                oldProfileName -> it.copy(name = newProfileName)
                else -> it
            }
        }
    }

    fun duplicateTemporaryProfile(profileName: String) {
        temporaryProfiles.first { it.name == profileName }.let { profile ->
            temporaryProfiles.add(profile.copy(name = findAvailableNewName("${profile.name} (Copy)")))
        }
    }

    fun replaceTemporaryProfile(profile: SettingsHeroProfile) {
        temporaryProfiles.replaceAll {
            when (it.name) {
                profile.name -> profile
                else -> it
            }
        }
    }

    fun saveTemporaryProfiles() {
        persistenceProfiles.names.toSet()
            .minus(temporaryProfiles.names.toSet())
            .forEach(localRepository::remove)
        persistenceProfiles.clearAndAddAll(temporaryProfiles)
        localRepository.saveProfiles(temporaryProfiles)
    }

    fun isModified(): Boolean = persistenceProfiles != temporaryProfiles

    fun resetTemporaryProfiles() {
        temporaryProfiles.clearAndAddAll(persistenceProfiles)
    }

    private fun prepareInitialData(): SettingsHeroProfiles {
        localRepository.saveProfiles(DefaultSettingsHeroProfiles)
        localRepository.profileDirectory(SettingsHeroProfile.DefaultName).createDirectories()
        persistenceProfiles.add(SettingsHeroProfile.Default)
        temporaryProfiles.add(SettingsHeroProfile.Default)
        return DefaultSettingsHeroProfiles
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
}
