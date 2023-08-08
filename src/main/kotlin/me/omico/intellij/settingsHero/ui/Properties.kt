// Copyright 2023 Omico
// SPDX-License-Identifier: GPL-3.0-only
package me.omico.intellij.settingsHero.ui

import com.intellij.openapi.observable.properties.GraphProperty
import com.intellij.openapi.observable.properties.ObservableProperty
import com.intellij.openapi.observable.properties.PropertyGraph
import com.intellij.openapi.observable.util.and
import com.intellij.openapi.observable.util.equalsTo
import me.omico.intellij.settingsHero.SettingsHeroRepositoryType
import me.omico.intellij.settingsHero.profile.SettingsHeroProfile
import me.omico.intellij.settingsHero.settingsHeroSettings
import me.omico.intellij.settingsHero.utility.onChanged

internal val propertyGraph = PropertyGraph()

internal val isSettingsHeroEnabledProperty: GraphProperty<Boolean> =
    propertyGraph.property(settingsHeroSettings.enabled)
        .onChanged { settingsHeroSettings.enabled = it }

internal val repositoryTypeProperty: GraphProperty<SettingsHeroRepositoryType> =
    propertyGraph.property(settingsHeroSettings.repositoryType)
        .onChanged { settingsHeroSettings.repositoryType = it }

internal val isLocalRepositoryProperty: ObservableProperty<Boolean> =
    repositoryTypeProperty.equalsTo(SettingsHeroRepositoryType.LOCAL)

internal val isRemoteRepositoryProperty: ObservableProperty<Boolean> =
    repositoryTypeProperty.equalsTo(SettingsHeroRepositoryType.REMOTE)

internal val localRepositoryDirectoryProperty: GraphProperty<String> =
    propertyGraph.property(settingsHeroSettings.localRepositoryDirectory)
        .onChanged { settingsHeroSettings.localRepositoryDirectory = it }

internal val remoteRepositoryUrlProperty: GraphProperty<String> =
    propertyGraph.property(settingsHeroSettings.remoteRepositoryUrl)
        .onChanged { settingsHeroSettings.remoteRepositoryUrl = it }

internal val isRepositoryAvailableProperty: GraphProperty<Boolean> = propertyGraph.property(false)

internal val isProfileGroupVisibleProperty: ObservableProperty<Boolean> =
    isSettingsHeroEnabledProperty.and(isRepositoryAvailableProperty)

internal val currentProfileProperty: GraphProperty<SettingsHeroProfile> = propertyGraph.lateinitProperty()
