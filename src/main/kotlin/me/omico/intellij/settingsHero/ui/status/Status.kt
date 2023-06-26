package me.omico.intellij.settingsHero.ui.status

import com.intellij.openapi.observable.util.not
import com.intellij.ui.dsl.builder.Panel
import me.omico.intellij.settingsHero.message
import me.omico.intellij.settingsHero.ui.component.button
import me.omico.intellij.settingsHero.ui.component.label
import me.omico.intellij.settingsHero.ui.isSettingsHeroEnabledProperty

internal fun Panel.status() {
    row {
        label(
            modifier = { visibleIf(isSettingsHeroEnabledProperty) },
            text = message("settingsHero.label.enabled"),
        )
    }
    row {
        label(
            modifier = { visibleIf(isSettingsHeroEnabledProperty.not()) },
            text = message("settingsHero.label.disabled"),
        )
    }
    row {
        button(
            modifier = { visibleIf(isSettingsHeroEnabledProperty.not()) },
            text = message("settingsHero.button.enable"),
            onClick = { isSettingsHeroEnabledProperty.set(true) },
        )
    }
    row {
        button(
            modifier = { visibleIf(isSettingsHeroEnabledProperty) },
            text = message("settingsHero.button.disable"),
            onClick = { isSettingsHeroEnabledProperty.set(false) },
        )
    }
}
