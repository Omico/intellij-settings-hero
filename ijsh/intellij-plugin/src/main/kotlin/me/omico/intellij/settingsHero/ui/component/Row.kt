// Copyright 2023-2024 Omico
// SPDX-License-Identifier: GPL-3.0-only
@file:Suppress("UnstableApiUsage")

package me.omico.intellij.settingsHero.ui.component

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.impl.ActionButton
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.openapi.util.NlsContexts
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.components.JBTextField
import com.intellij.ui.dsl.builder.Cell
import com.intellij.ui.dsl.builder.Row
import com.intellij.ui.dsl.builder.SegmentedButton
import com.intellij.ui.dsl.builder.actionButton
import com.intellij.ui.dsl.builder.actionsButton
import org.jetbrains.annotations.NonNls
import java.awt.event.ActionEvent
import javax.swing.ComboBoxModel
import javax.swing.Icon
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.ListCellRenderer

fun Row.button(
    modifier: Cell<JButton>.() -> Unit = {},
    @NlsContexts.Button text: String,
    onClick: (event: ActionEvent) -> Unit,
) {
    button(
        text = text,
        actionListener = onClick,
    ).apply(modifier)
}

fun Row.actionButton(
    modifier: Cell<ActionButton>.() -> Unit = {},
    @NonNls actionPlace: String = ActionPlaces.UNKNOWN,
    action: AnAction,
) {
    actionButton(
        action = action,
        actionPlace = actionPlace,
    ).apply(modifier)
}

fun Row.actionsButton(
    modifier: Cell<ActionButton>.() -> Unit = {},
    vararg actions: AnAction,
    @NonNls actionPlace: String = ActionPlaces.UNKNOWN,
    icon: Icon = AllIcons.General.GearPlain,
) {
    actionsButton(
        actions = actions,
        actionPlace = actionPlace,
        icon = icon,
    ).apply(modifier)
}

fun <T> Row.segmentedButton(
    modifier: SegmentedButton<T>.() -> Unit = {},
    items: Collection<T>,
    renderer: SegmentedButton.ItemPresentation.(T) -> Unit,
) {
    segmentedButton(
        items = items,
        renderer = renderer,
    ).apply(modifier)
}

fun Row.label(
    modifier: Cell<JLabel>.() -> Unit = {},
    @NlsContexts.Label text: String = "",
): Cell<JLabel> =
    label(text = text).apply(modifier)

fun Row.textField(
    modifier: Cell<JBTextField>.() -> Unit = {},
) {
    textField().apply(modifier)
}

fun Row.textFieldWithBrowseButton(
    modifier: Cell<TextFieldWithBrowseButton>.() -> Unit = {},
    browseDialogTitle: @NlsContexts.DialogTitle String? = null,
    project: Project? = null,
    fileChooserDescriptor: FileChooserDescriptor = FileChooserDescriptorFactory.createSingleFileNoJarsDescriptor(),
    fileChosen: ((chosenFile: VirtualFile) -> String)? = null,
) {
    textFieldWithBrowseButton(
        fileChooserDescriptor = fileChooserDescriptor.withTitle(browseDialogTitle),
        project = project,
        fileChosen = fileChosen,
    ).apply(modifier)
}

fun <T> Row.comboBox(
    modifier: Cell<ComboBox<T>>.() -> Unit = {},
    model: ComboBoxModel<T>,
    renderer: ListCellRenderer<in T?>? = null,
) {
    comboBox(
        model = model,
        renderer = renderer,
    ).apply(modifier)
}
