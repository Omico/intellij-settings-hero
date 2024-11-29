// Copyright 2023-2024 Omico
// SPDX-License-Identifier: GPL-3.0-only
package me.omico.intellij.settingsHero

import com.intellij.openapi.util.IconLoader

object SettingsHeroIcons {
    private fun loadIcon(path: String) = IconLoader.getIcon(path, SettingsHeroIcons::class.java)

    object Actions {
        val Settings = loadIcon("/icons/actions/settings.svg")
        val OpenInNewWindow = loadIcon("/icons/actions/openInNewWindow.svg")
    }
}
