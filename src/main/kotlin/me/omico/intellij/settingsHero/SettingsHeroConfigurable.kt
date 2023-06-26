@file:Suppress("UnstableApiUsage")

package me.omico.intellij.settingsHero

import com.intellij.openapi.options.BoundSearchableConfigurable
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.dsl.builder.panel
import me.omico.intellij.settingsHero.ui.profile.profile
import me.omico.intellij.settingsHero.ui.repository.repository
import me.omico.intellij.settingsHero.ui.status.status

internal class SettingsHeroConfigurable : BoundSearchableConfigurable(
    displayName = message("settingsHero.title"),
    helpTopic = message("settingsHero.title"),
) {
    override fun createPanel(): DialogPanel = panel {
        status()
        repository()
        profile(parentDisposable = disposable!!)
    }
}
