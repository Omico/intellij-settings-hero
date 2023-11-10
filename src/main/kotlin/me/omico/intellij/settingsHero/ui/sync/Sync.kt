// Copyright 2023 Omico
// SPDX-License-Identifier: GPL-3.0-only
package me.omico.intellij.settingsHero.ui.sync

import com.intellij.ui.dsl.builder.Panel
import me.omico.intellij.settingsHero.message
import me.omico.intellij.settingsHero.ui.isSettingsHeroEnabledProperty

internal fun Panel.operation() {
    group(title = message("settingsHero.group.operation.title")) {
        visibleIf(isSettingsHeroEnabledProperty)
        saveToLocal()
    }
}
