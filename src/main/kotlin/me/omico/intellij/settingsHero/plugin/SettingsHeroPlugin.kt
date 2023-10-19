// Copyright 2023 Omico
// SPDX-License-Identifier: GPL-3.0-only
package me.omico.intellij.settingsHero.plugin

import com.intellij.ide.plugins.IdeaPluginDependency
import com.intellij.ide.plugins.IdeaPluginDescriptor
import kotlinx.serialization.Serializable

@Serializable
data class SettingsHeroPlugin(
    val id: String,
    val name: String,
    val enabled: Boolean = true,
    val dependencies: SettingsHeroPluginDependencies = emptyList(),
)

typealias SettingsHeroPlugins = List<SettingsHeroPlugin>

internal fun IdeaPluginDescriptor.toSettingsHeroPlugin(): SettingsHeroPlugin =
    SettingsHeroPlugin(
        id = pluginId.idString,
        name = name,
        enabled = isEnabled,
        dependencies = dependencies.map(IdeaPluginDependency::toSettingsHeroPluginDependency),
    )
