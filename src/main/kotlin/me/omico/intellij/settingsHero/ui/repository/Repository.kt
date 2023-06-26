@file:Suppress("UnstableApiUsage")

package me.omico.intellij.settingsHero.ui.repository

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.observable.properties.ObservableMutableProperty
import com.intellij.openapi.observable.properties.ObservableProperty
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.ui.components.JBTextField
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.Cell
import com.intellij.ui.dsl.builder.Panel
import com.intellij.ui.layout.ValidationInfoBuilder
import me.omico.intellij.settingsHero.SettingsHeroRepositoryType
import me.omico.intellij.settingsHero.message
import me.omico.intellij.settingsHero.profile.SettingsHeroProfileManager
import me.omico.intellij.settingsHero.repository.refreshLocalRepository
import me.omico.intellij.settingsHero.ui.component.group
import me.omico.intellij.settingsHero.ui.component.segmentedButton
import me.omico.intellij.settingsHero.ui.component.textField
import me.omico.intellij.settingsHero.ui.component.textFieldWithBrowseButton
import me.omico.intellij.settingsHero.ui.isLocalRepositoryProperty
import me.omico.intellij.settingsHero.ui.isRemoteRepositoryProperty
import me.omico.intellij.settingsHero.ui.isRepositoryAvailableProperty
import me.omico.intellij.settingsHero.ui.isSettingsHeroEnabledProperty
import me.omico.intellij.settingsHero.ui.localRepositoryDirectoryProperty
import me.omico.intellij.settingsHero.ui.remoteRepositoryUrlProperty
import me.omico.intellij.settingsHero.ui.repositoryTypeProperty
import javax.swing.JComponent

fun Panel.repository(): Unit =
    group(
        modifier = { visibleIf(isSettingsHeroEnabledProperty) },
        title = message("settingsHero.group.repository.title"),
        init = {
            if (isSettingsHeroEnabledProperty.get()) {
                checkRepositoryAvailability(repositoryTypeProperty.get())
            }
            isSettingsHeroEnabledProperty.afterChange { isEnabled ->
                if (isEnabled) checkRepositoryAvailability(repositoryTypeProperty.get())
            }
            row {
                label(message("settingsHero.label.repository"))
            }
            row {
                segmentedButton(
                    modifier = {
                        selectedItem = repositoryTypeProperty.get()
                        bind(repositoryTypeProperty)
                        whenItemSelectedFromUi(listener = ::checkRepositoryAvailability)
                    },
                    items = SettingsHeroRepositoryType.values().toList(),
                    renderer = SettingsHeroRepositoryType::displayName,
                )
            }
            row {
                textFieldWithBrowseButton(
                    modifier = {
                        applyRepositoryTextField(
                            repository = localRepositoryDirectoryProperty,
                            visibleIf = isLocalRepositoryProperty,
                            initializeText = { text = it },
                            textUpdater = TextFieldWithBrowseButton::getText,
                            validation = { localRepositoryPathValidationInfo(it.text) },
                        )
                    },
                    browseDialogTitle = message("settingsHero.textField.repository.browseDialogTitle"),
                    fileChooserDescriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor(),
                )
            }
            row {
                textField(
                    modifier = {
                        applyRepositoryTextField(
                            repository = remoteRepositoryUrlProperty,
                            visibleIf = isRemoteRepositoryProperty,
                            initializeText = { text = it },
                            textUpdater = JBTextField::getText,
                            validation = { remoteRepositoryUrlValidationInfo(it.text) },
                        )
                    },
                )
            }
        },
    )

private fun checkRepositoryAvailability(type: SettingsHeroRepositoryType) {
    val message = when (type) {
        SettingsHeroRepositoryType.LOCAL -> localRepositoryPathValidationMessage(localRepositoryDirectoryProperty.get())
        SettingsHeroRepositoryType.REMOTE -> remoteRepositoryUrlValidationMessage(remoteRepositoryUrlProperty.get())
    }
    val isAvailable = message == null
    isRepositoryAvailableProperty.set(isAvailable)
    if (!isAvailable) return
    refreshLocalRepository()
    SettingsHeroProfileManager.load()
}

private fun <T : JComponent> Cell<T>.applyRepositoryTextField(
    repository: ObservableMutableProperty<String>,
    visibleIf: ObservableProperty<Boolean>,
    initializeText: T.(text: String) -> Unit,
    textUpdater: T.() -> String,
    validation: ValidationInfoBuilder.(T) -> ValidationInfo?,
) {
    var currentRepository = repository.get()
    visibleIf(visibleIf)
    align(Align.FILL)
    applyToComponent {
        initializeText(currentRepository)
    }
    onChanged { currentRepository = textUpdater(it) }
    validationInfo(validation)
    onIsModified { repository.get() != currentRepository }
    onApply {
        repository.set(currentRepository)
        checkRepositoryAvailability(repositoryTypeProperty.get())
    }
    onReset { currentRepository = repository.get() }
}
