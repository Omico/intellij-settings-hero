// Copyright 2023 Omico
// SPDX-License-Identifier: GPL-3.0-only
package me.omico.intellij.settingsHero.plugin

import com.intellij.ide.plugins.IdeaPluginDependency
import kotlinx.serialization.Serializable

@Serializable
data class SettingsHeroPluginDependency(
    val id: String,
    val isOptional: Boolean = false,
)

typealias SettingsHeroPluginDependencies = List<SettingsHeroPluginDependency>

internal fun IdeaPluginDependency.toSettingsHeroPluginDependency(): SettingsHeroPluginDependency =
    SettingsHeroPluginDependency(
        id = pluginId.idString,
        isOptional = isOptional,
    )
