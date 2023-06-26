package me.omico.intellij.settingsHero.ui.profile

import com.intellij.collaboration.ui.selectFirst
import com.intellij.openapi.observable.properties.GraphProperty
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.ui.CollectionComboBoxModel
import com.intellij.ui.dsl.builder.Panel
import com.intellij.ui.dsl.builder.bindItem
import com.intellij.ui.util.preferredWidth
import me.omico.intellij.settingsHero.SettingsHeroIcons
import me.omico.intellij.settingsHero.message
import me.omico.intellij.settingsHero.profile.DefaultSettingsHeroProfiles
import me.omico.intellij.settingsHero.profile.SettingsHeroProfileManager
import me.omico.intellij.settingsHero.profile.SettingsHeroProfiles
import me.omico.intellij.settingsHero.profile.names
import me.omico.intellij.settingsHero.settingsHeroSettings
import me.omico.intellij.settingsHero.ui.component.actionsButton
import me.omico.intellij.settingsHero.ui.component.comboBox
import me.omico.intellij.settingsHero.ui.component.showTextFieldDialog
import me.omico.intellij.settingsHero.ui.currentProfileProperty
import me.omico.intellij.settingsHero.ui.isSettingsHeroEnabledProperty
import me.omico.intellij.settingsHero.ui.propertyGraph
import me.omico.intellij.settingsHero.utility.onChanged

private val currentProfileNameProperty: GraphProperty<String> =
    propertyGraph.property(settingsHeroSettings.currentProfile)
        .onChanged { currentProfile ->
            settingsHeroSettings.currentProfile = currentProfile
            currentProfileProperty.set(SettingsHeroProfileManager.temporaryProfile(currentProfile))
        }

private val profilesProperty: GraphProperty<SettingsHeroProfiles> =
    propertyGraph.lazyProperty(::DefaultSettingsHeroProfiles)
        .onChanged { profiles -> ProfileComboBoxModel.reload(profiles.names) }

internal fun Panel.currentProfile() {
    isSettingsHeroEnabledProperty.afterChange { isEnabled ->
        if (!isEnabled) return@afterChange
        updateProfiles(
            after = { currentProfileNameProperty.set(settingsHeroSettings.currentProfile) },
        )
    }
    row(label = message("settingsHero.label.currentProfile")) {
        comboBox(
            modifier = {
                applyToComponent {
                    preferredWidth = 300
                }
                bindItem(currentProfileNameProperty)
                ProfileComboBoxModel.selectedItem = currentProfileNameProperty.get()
                onIsModified {
                    when {
                        !isSettingsHeroEnabledProperty.get() -> false
                        else -> currentProfileNameProperty.get() != ProfileComboBoxModel.selectedItem
                    }
                }
                onApply { currentProfileNameProperty.set(ProfileComboBoxModel.selectedItem!!) }
                onReset { currentProfileNameProperty.set(settingsHeroSettings.currentProfile) }
            },
            model = ProfileComboBoxModel,
        )
        actionsButton(
            modifier = {
                onIsModified(SettingsHeroProfileManager::isModified)
                onApply { updateProfiles(before = SettingsHeroProfileManager::saveTemporaryProfiles) }
                onReset { updateProfiles(before = SettingsHeroProfileManager::resetTemporaryProfiles) }
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
                val profile = SettingsHeroProfileManager.newTemporaryProfile()
                ProfileComboBoxModel.new(profile.name)
            },
        )
    },
    DumbAwareAction.create(message("settingsHero.action.removeProfile")) {
        updateProfiles(
            before = { SettingsHeroProfileManager.removeTemporaryProfile(currentProfileNameProperty.get()) },
            after = ProfileComboBoxModel::selectFirst,
        )
    },
    DumbAwareAction.create(message("settingsHero.action.renameProfile")) {
        updateProfiles(
            before = {
                val oldProfileName = currentProfileNameProperty.get()
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
                                    SettingsHeroProfileManager.renameTemporaryProfile(oldProfileName, newProfileName)
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
            before = { SettingsHeroProfileManager.duplicateTemporaryProfile(currentProfileNameProperty.get()) },
        )
    },
)

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
        addAll(0, names.sorted())
    }
}
