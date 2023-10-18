// Copyright 2023 Omico
// SPDX-License-Identifier: GPL-3.0-only
package me.omico.intellij.settingsHero.ui.profile

import com.intellij.openapi.Disposable
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.openapi.util.NlsContexts
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.LabelPosition
import com.intellij.ui.dsl.builder.Panel
import com.intellij.ui.dsl.builder.Row
import me.omico.intellij.settingsHero.message
import me.omico.intellij.settingsHero.profile.saveSettings
import me.omico.intellij.settingsHero.ui.component.addDumbAwareAction
import me.omico.intellij.settingsHero.ui.component.fileTree
import me.omico.intellij.settingsHero.ui.component.group
import me.omico.intellij.settingsHero.ui.component.showTextFieldDialog
import me.omico.intellij.settingsHero.ui.currentProfileProperty
import me.omico.intellij.settingsHero.ui.isProfileGroupVisibleProperty
import me.omico.intellij.settingsHero.utility.ideaConfigurationDirectory
import me.omico.intellij.settingsHero.utility.ideaConfigurationDirectoryPathString
import me.omico.intellij.settingsHero.utility.localFileSystem
import java.awt.Desktop
import java.io.File
import java.nio.file.Path
import kotlin.io.path.createTempDirectory
import kotlin.io.path.invariantSeparatorsPathString

internal fun Panel.profile(
    parentDisposable: Disposable,
): Unit =
    group(
        modifier = { visibleIf(isProfileGroupVisibleProperty) },
        title = message("settingsHero.group.profile.title"),
    ) {
        currentProfile()
        row {
            currentProfileProperty.afterChange { visible(it != null) }
            rulesProperty.afterChange(temporaryProfileConfigurationDirectory::saveSettings)
            configurationFileTree(
                label = message("settingsHero.label.localConfigurations"),
                parentDisposable = parentDisposable,
                configurationDirectory = ideaConfigurationDirectory,
            )
            configurationFileTree(
                label = message("settingsHero.label.preview"),
                parentDisposable = parentDisposable,
                configurationDirectory = temporaryProfileConfigurationDirectory,
            )
            rules()
        }
    }

private fun Row.configurationFileTree(
    @NlsContexts.Label label: String,
    parentDisposable: Disposable,
    configurationDirectory: Path,
) {
    fileTree(
        modifier = {
            label(label = label, position = LabelPosition.TOP)
            align(Align.FILL)
        },
        parentDisposable = parentDisposable,
        fileChooserDescriptor = run {
            FileChooserDescriptorFactory
                .createSingleFileOrFolderDescriptor()
                .withRoots(localFileSystem.findFileByNioFile(configurationDirectory))
        },
        popupGroup = { fileSystemTree ->
            addDumbAwareAction(text = message("settingsHero.action.addRule")) {
                fileSystemTree.selectedFile?.let(::showAddRuleDialog)
            }
            addDumbAwareAction(text = message("settingsHero.action.previewFile")) {
                fileSystemTree.selectedFile?.let { selectedFile ->
                    // TODO find a way to open IDEA light editor
                    Desktop.getDesktop().open(File(selectedFile.path))
                }
            }
        },
    )
}

private val temporaryProfileConfigurationDirectory: Path = createTempDirectory("settings-hero-")
private val temporaryProfileConfigurationDirectoryPathString: String =
    temporaryProfileConfigurationDirectory.invariantSeparatorsPathString

private fun showAddRuleDialog(selectedFile: VirtualFile): Unit =
    showTextFieldDialog(
        title = "Add Rule",
        initialText = run {
            selectedFile.path
                .removePrefix(ideaConfigurationDirectoryPathString)
                .removePrefix(temporaryProfileConfigurationDirectoryPathString)
                .removePrefix("/")
        },
        ok = { rule ->
            buildList {
                when (rule) {
                    null -> ValidationInfo("Rule cannot be empty")
                    else -> {
                        val newRules = rulesProperty.get() + rule
                        rulesProperty.set(newRules.toSortedSet())
                    }
                }
            }
        },
    )
