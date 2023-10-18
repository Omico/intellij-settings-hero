// Copyright 2023 Omico
// SPDX-License-Identifier: GPL-3.0-only
package me.omico.intellij.settingsHero.profile

import kotlinx.serialization.Serializable

typealias SettingsHeroProfiles = List<SettingsHeroProfile>

val SettingsHeroProfiles.names: List<String>
    get() = map(SettingsHeroProfile::name)

@Serializable
data class SettingsHeroProfile(
    val name: String = DEFAULT_NAME,
    val rules: Set<String> = DefaultRegexes,
) {
    companion object {
        internal const val DEFAULT_NAME = "Default"

        private val DefaultRegexes = setOf(
            "codestyles/*",
            "colors/*",
            "keymaps/*",
            "options/*",
        )

        val Default = SettingsHeroProfile()
    }
}

internal val DefaultSettingsHeroProfiles: List<SettingsHeroProfile> = listOf(SettingsHeroProfile.Default)
