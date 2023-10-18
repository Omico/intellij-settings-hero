// Copyright 2023 Omico
// SPDX-License-Identifier: GPL-3.0-only
package me.omico.intellij.settingsHero.ui.profile

import com.intellij.openapi.observable.properties.GraphProperty
import com.intellij.ui.dsl.builder.Row
import me.omico.intellij.settingsHero.message
import me.omico.intellij.settingsHero.profile.DefaultRules
import me.omico.intellij.settingsHero.profile.SettingsHeroProfileManager
import me.omico.intellij.settingsHero.ui.component.rulesList
import me.omico.intellij.settingsHero.ui.currentProfileProperty
import me.omico.intellij.settingsHero.ui.propertyGraph

internal val rulesProperty: GraphProperty<Set<String>> = propertyGraph.property(DefaultRules)

internal fun Row.rules() {
    currentProfileProperty.afterChange { profile ->
        rulesProperty.set(profile?.settingsGitIgnoreRules ?: DefaultRules)
    }
    rulesProperty.afterChange { rules ->
        val profile = currentProfileProperty.get() ?: return@afterChange
        if (profile.settingsGitIgnoreRules == rules) return@afterChange
        val newProfile = profile.copy(settingsGitIgnoreRules = rules)
        updateProfiles(
            before = {
                SettingsHeroProfileManager.replace(newProfile)
                SettingsHeroProfileManager.save()
            },
            after = { currentProfileProperty.set(newProfile) },
        )
    }
    rulesList(
        label = message("settingsHero.label.rules"),
        rulesProperty = rulesProperty,
    )
}
