// Copyright 2023 Omico
// SPDX-License-Identifier: GPL-3.0-only
package me.omico.intellij.settingsHero.ui.profile

import com.intellij.openapi.observable.properties.GraphProperty
import com.intellij.openapi.observable.util.whenItemSelected
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.ui.CollectionComboBoxModel
import com.intellij.ui.dsl.builder.Panel
import com.intellij.ui.util.preferredWidth
import me.omico.intellij.settingsHero.SettingsHeroIcons
import me.omico.intellij.settingsHero.message
import me.omico.intellij.settingsHero.profile.SettingsHeroProfile
import me.omico.intellij.settingsHero.profile.SettingsHeroProfileManager
import me.omico.intellij.settingsHero.profile.SettingsHeroProfiles
import me.omico.intellij.settingsHero.profile.names
import me.omico.intellij.settingsHero.settingsHeroSettings
import me.omico.intellij.settingsHero.ui.component.actionsButton
import me.omico.intellij.settingsHero.ui.component.comboBox
import me.omico.intellij.settingsHero.ui.component.showTextFieldDialog
import me.omico.intellij.settingsHero.ui.currentProfileProperty
import me.omico.intellij.settingsHero.ui.isSettingsHeroEnabledProperty
import me.omico.intellij.settingsHero.ui.localRepositoryDirectoryProperty
import me.omico.intellij.settingsHero.ui.propertyGraph
import me.omico.intellij.settingsHero.utility.onChanged
import kotlin.io.path.Path

private val currentProfileNameProperty: GraphProperty<String?> =
    propertyGraph.property(settingsHeroSettings.currentProfile)
        .onChanged { currentProfile ->
            settingsHeroSettings.currentProfile = currentProfile
            currentProfileProperty.set(currentProfile?.let(SettingsHeroProfileManager::find))
        }

private val profilesProperty: GraphProperty<SettingsHeroProfiles> =
    propertyGraph.property(emptyList<SettingsHeroProfile>())
        .onChanged { profiles ->
            ProfileComboBoxModel.reload(profiles.names)
            ProfileComboBoxModel.selectedItem = currentProfileNameProperty.get()
        }

internal fun Panel.currentProfile() {
    isSettingsHeroEnabledProperty.afterChange { isEnabled ->
        if (!isEnabled) return@afterChange
        updateProfiles(
            after = { currentProfileNameProperty.set(settingsHeroSettings.currentProfile) },
        )
    }
    localRepositoryDirectoryProperty.afterChange {
        updateProfiles(
            before = { SettingsHeroProfileManager.initialize(Path(it)) },
            after = { currentProfileNameProperty.set(settingsHeroSettings.currentProfile) },
        )
    }
    row(label = message("settingsHero.label.currentProfile")) {
        comboBox(
            modifier = {
                applyToComponent {
                    preferredWidth = 300
                    whenItemSelected { item ->
                        ProfileComboBoxModel.selectedItem = item
                    }
                }
                onIsModified {
                    when {
                        !isSettingsHeroEnabledProperty.get() -> false
                        else -> currentProfileNameProperty.get() != ProfileComboBoxModel.selectedItem
                    }
                }
                onApply { currentProfileNameProperty.set(ProfileComboBoxModel.selectedItem) }
                onReset { currentProfileNameProperty.set(settingsHeroSettings.currentProfile) }
            },
            model = ProfileComboBoxModel,
        )
        actionsButton(
            modifier = {
                onIsModified(SettingsHeroProfileManager::isModified)
                onApply { updateProfiles(before = SettingsHeroProfileManager::save) }
                onReset { updateProfiles(before = SettingsHeroProfileManager::reset) }
            },
            actions = profileActions,
            icon = SettingsHeroIcons.Actions.Settings,
        )
    }
}

internal fun updateProfiles(
    before: () -> Unit = {},
    after: () -> Unit = {},
) {
    before()
    profilesProperty.set(SettingsHeroProfileManager.profiles)
    after()
}

private val profileActions = arrayOf(
    DumbAwareAction.create(message("settingsHero.action.newProfile")) {
        updateProfiles(
            before = {
                val profile = SettingsHeroProfileManager.new()
                ProfileComboBoxModel.new(profile.name)
                currentProfileNameProperty.set(profile.name)
            },
        )
    },
    DumbAwareAction.create(message("settingsHero.action.removeProfile")) {
        updateProfiles(
            before = {
                val profileName = currentProfileNameProperty.get() ?: return@updateProfiles
                SettingsHeroProfileManager.remove(profileName)
            },
            after = {
                val names = SettingsHeroProfileManager.profiles.names
                ProfileComboBoxModel.reload(SettingsHeroProfileManager.profiles.names)
                ProfileComboBoxModel.selectedItem = names.firstOrNull()
                currentProfileNameProperty.set(ProfileComboBoxModel.selectedItem)
            },
        )
    },
    DumbAwareAction.create(message("settingsHero.action.renameProfile")) {
        updateProfiles(
            before = {
                val oldProfileName = currentProfileNameProperty.get() ?: return@updateProfiles
                showTextFieldDialog(
                    title = message("settingsHero.dialog.renameProfile.title"),
                    initialText = oldProfileName,
                    ok = { newProfileName ->
                        buildList {
                            when (newProfileName) {
                                oldProfileName -> return@buildList
                                null -> ValidationInfo("Profile name cannot be empty").let(::add)
                                in SettingsHeroProfileManager.profiles.names ->
                                    ValidationInfo("Profile name already exists").let(::add)
                                else -> {
                                    ProfileComboBoxModel.rename(oldProfileName, newProfileName)
                                    SettingsHeroProfileManager.rename(oldProfileName, newProfileName)
                                    currentProfileNameProperty.set(newProfileName)
                                }
                            }
                        }
                    },
                )
            },
        )
    },
    DumbAwareAction.create(message("settingsHero.action.duplicateProfile")) {
        updateProfiles(
            before = {
                val profileName = currentProfileNameProperty.get() ?: return@updateProfiles
                SettingsHeroProfileManager.duplicate(profileName)
            },
        )
    },
)

@Suppress("JavaIoSerializableObjectMustHaveReadResolve")
private object ProfileComboBoxModel : CollectionComboBoxModel<String>() {
    override fun getSelectedItem(): String? = super.getSelectedItem() as? String

    fun new(name: String) {
        add(name)
        selectedItem = name
    }

    fun rename(oldName: String, newName: String) {
        remove(oldName)
        add(newName)
        selectedItem = newName
    }

    fun reload(names: List<String>) {
        removeAll()
        addAll(0, names)
    }
}
