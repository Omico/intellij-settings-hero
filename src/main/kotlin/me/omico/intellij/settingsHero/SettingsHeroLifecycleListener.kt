// Copyright 2023 Omico
// SPDX-License-Identifier: GPL-3.0-only
@file:Suppress("UnstableApiUsage")

package me.omico.intellij.settingsHero

import com.intellij.ide.AppLifecycleListener
import me.omico.intellij.settingsHero.profile.SettingsHeroProfileManager
import kotlin.io.path.Path

internal class SettingsHeroLifecycleListener : AppLifecycleListener {
    override fun appFrameCreated(commandLineArgs: MutableList<String>) {
        if (!settingsHeroSettings.enabled) return
        if (settingsHeroSettings.localRepositoryDirectory.isBlank()) return
        SettingsHeroProfileManager.initialize(Path(settingsHeroSettings.localRepositoryDirectory))
    }

    override fun appClosing() {
        if (!settingsHeroSettings.enabled) return
        SettingsHeroProfileManager.save()
    }
}
