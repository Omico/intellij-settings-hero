package me.omico.intellij.settingsHero.profile

import kotlinx.serialization.Serializable

typealias SettingsHeroProfiles = List<SettingsHeroProfile>

val SettingsHeroProfiles.names
    get() = map(SettingsHeroProfile::name)

@Serializable
data class SettingsHeroProfile(
    val name: String = DefaultName,
    val rules: Set<String> = DefaultRegexes,
) {
    companion object {
        internal const val DefaultName = "Default"

        private val DefaultRegexes = setOf(
            "codestyles/*",
            "colors/*",
            "keymaps/*",
            "options/*",
        )

        val Default = SettingsHeroProfile()

        val Empty = SettingsHeroProfile(name = "Empty", rules = emptySet())
    }
}

internal val DefaultSettingsHeroProfiles = listOf(SettingsHeroProfile.Default)
