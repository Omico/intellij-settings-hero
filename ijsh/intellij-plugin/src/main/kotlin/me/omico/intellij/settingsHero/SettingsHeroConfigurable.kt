// Copyright 2023-2024 Omico
// SPDX-License-Identifier: GPL-3.0-only
package me.omico.intellij.settingsHero

import com.intellij.openapi.options.BoundSearchableConfigurable
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.dsl.builder.panel
import me.omico.intellij.settingsHero.profile.SettingsHeroProfileManager
import me.omico.intellij.settingsHero.ui.profile.profile
import me.omico.intellij.settingsHero.ui.repository.repository
import me.omico.intellij.settingsHero.ui.status.status
import me.omico.intellij.settingsHero.ui.sync.operation

internal class SettingsHeroConfigurable :
    BoundSearchableConfigurable(
        displayName = message("settingsHero.title"),
        helpTopic = message("settingsHero.title"),
    ) {
    override fun createPanel(): DialogPanel = panel {
        onApply(SettingsHeroProfileManager::save)
        status()
        operation()
        repository()
        profile(parentDisposable = disposable!!)
    }
}
