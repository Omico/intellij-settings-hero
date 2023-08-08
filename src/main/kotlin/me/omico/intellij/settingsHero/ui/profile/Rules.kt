// Copyright 2023 Omico
// SPDX-License-Identifier: GPL-3.0-only
package me.omico.intellij.settingsHero.ui.profile

import com.intellij.openapi.observable.properties.GraphProperty
import com.intellij.ui.dsl.builder.Row
import me.omico.intellij.settingsHero.message
import me.omico.intellij.settingsHero.profile.SettingsHeroProfileManager
import me.omico.intellij.settingsHero.ui.component.rulesList
import me.omico.intellij.settingsHero.ui.currentProfileProperty
import me.omico.intellij.settingsHero.ui.propertyGraph

internal val rulesProperty: GraphProperty<Set<String>> = propertyGraph.property(emptySet())

internal fun Row.rules() {
    currentProfileProperty.afterChange {
        rulesProperty.set(it.rules)
    }
    rulesProperty.afterChange { rules ->
        val profile = currentProfileProperty.get()
        if (profile.rules == rules) return@afterChange
        val newProfile = profile.copy(rules = rules)
        updateProfiles(
            before = {
                SettingsHeroProfileManager.replaceTemporaryProfile(newProfile)
                SettingsHeroProfileManager.saveTemporaryProfiles()
            },
            after = { currentProfileProperty.set(newProfile) },
        )
    }
    rulesList(
        label = message("settingsHero.label.rules"),
        rulesProperty = rulesProperty,
    )
}
