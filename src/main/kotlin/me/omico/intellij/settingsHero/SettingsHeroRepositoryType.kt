// Copyright 2023 Omico
// SPDX-License-Identifier: GPL-3.0-only
package me.omico.intellij.settingsHero

enum class SettingsHeroRepositoryType(
    val displayName: String,
) {
    LOCAL(displayName = message("settingsHero.repositoryType.local")),
    REMOTE(displayName = message("settingsHero.repositoryType.remote")),
}
