// Copyright 2023-2024 Omico
// SPDX-License-Identifier: GPL-3.0-only
package me.omico.intellij.settingsHero.plugin

import com.intellij.ide.plugins.IdeaPluginDescriptor
import com.intellij.ide.plugins.PluginManagerCore
import me.omico.intellij.settingsHero.profile.SettingsHeroProfileConstants
import me.omico.intellij.settingsHero.utility.encodeAsPrettyJson
import java.nio.file.Path

object SettingsHeroPluginManager {
    fun save(profileRootDirectory: Path) {
        profileRootDirectory.resolve(SettingsHeroProfileConstants.PLUGINS_FILE_NAME).encodeAsPrettyJson(load())
    }

    private fun load(): SettingsHeroPlugins =
        PluginManagerCore.plugins
            .filterNot(IdeaPluginDescriptor::isBundled)
            .map(IdeaPluginDescriptor::toSettingsHeroPlugin)
            .sortedBy(SettingsHeroPlugin::id)
}
