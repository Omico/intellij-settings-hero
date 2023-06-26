package me.omico.intellij.settingsHero.plugin

import com.intellij.ide.plugins.IdeaPluginDescriptor
import com.intellij.ide.plugins.PluginManagerCore
import kotlinx.serialization.Serializable

@Serializable
data class PluginInfo(
    val name: String,
    val isEnabled: Boolean,
    val pluginId: String,
)

val plugins: List<PluginInfo>
    get() = PluginManagerCore.getPlugins()
        .mapNotNull {
            if (it.isBundled) return@mapNotNull null
            it.toPluginInfo()
        }
        .sortedBy(PluginInfo::pluginId)

private fun IdeaPluginDescriptor.toPluginInfo(): PluginInfo =
    PluginInfo(
        name = name,
        isEnabled = isEnabled,
        pluginId = pluginId.idString,
    )
