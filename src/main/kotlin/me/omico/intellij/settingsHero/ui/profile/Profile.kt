// Copyright 2023 Omico
// SPDX-License-Identifier: GPL-3.0-only
package me.omico.intellij.settingsHero.ui.profile

import com.intellij.openapi.Disposable
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.LabelPosition
import com.intellij.ui.dsl.builder.Panel
import me.omico.intellij.settingsHero.message
import me.omico.intellij.settingsHero.patternCache
import me.omico.intellij.settingsHero.repository.saveSettings
import me.omico.intellij.settingsHero.ui.component.addDumbAwareAction
import me.omico.intellij.settingsHero.ui.component.fileTree
import me.omico.intellij.settingsHero.ui.component.group
import me.omico.intellij.settingsHero.ui.component.showTextFieldDialog
import me.omico.intellij.settingsHero.ui.isProfileGroupVisibleProperty
import me.omico.intellij.settingsHero.utility.ideaConfigurationDirectory
import me.omico.intellij.settingsHero.utility.localFileSystem
import me.omico.intellij.settingsHero.utility.removeIdeaConfigurationDirectoryPrefix
import java.awt.Desktop
import java.io.File
import java.nio.file.Path
import kotlin.io.path.createTempDirectory

internal fun Panel.profile(
    parentDisposable: Disposable,
): Unit =
    group(
        modifier = { visibleIf(isProfileGroupVisibleProperty) },
        title = message("settingsHero.group.profile.title"),
    ) {
        currentProfile()
        row {
            rulesProperty.afterChange { rules ->
                temporaryProfileDirectory.saveSettings(patternCache, rules)
            }
            fileTree(
                modifier = {
                    label(label = message("settingsHero.label.localConfigurations"), position = LabelPosition.TOP)
                    align(Align.FILL)
                },
                parentDisposable = parentDisposable,
                fileChooserDescriptor = run {
                    FileChooserDescriptorFactory
                        .createSingleFileOrFolderDescriptor()
                        .withRoots(localFileSystem.findFileByNioFile(ideaConfigurationDirectory))
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
            fileTree(
                modifier = {
                    label(label = message("settingsHero.label.preview"), position = LabelPosition.TOP)
                    align(Align.FILL)
                },
                parentDisposable = parentDisposable,
                fileChooserDescriptor = run {
                    FileChooserDescriptorFactory
                        .createSingleFileOrFolderDescriptor()
                        .withRoots(localFileSystem.findFileByNioFile(temporaryProfileDirectory))
                },
            )
            rules()
        }
    }

private val temporaryProfileDirectory: Path = createTempDirectory("settings-hero-")

private fun showAddRuleDialog(selectedFile: VirtualFile): Unit =
    showTextFieldDialog(
        title = "Add Rule",
        initialText = selectedFile.path.removeIdeaConfigurationDirectoryPrefix(),
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
