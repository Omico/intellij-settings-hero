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
import org.jetbrains.annotations.Nls
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
    renderer: (T) -> @Nls String,
) {
    segmentedButton(
        items = items,
        renderer = renderer,
    ).apply(modifier)
}

fun Row.label(
    modifier: Cell<JLabel>.() -> Unit = {},
    @NlsContexts.Label text: String,
) {
    label(text = text).apply(modifier)
}

fun Row.textField(
    modifier: Cell<JBTextField>.() -> Unit = {},
) {
    textField().apply(modifier)
}

fun Row.textFieldWithBrowseButton(
    modifier: Cell<TextFieldWithBrowseButton>.() -> Unit = {},
    @NlsContexts.DialogTitle browseDialogTitle: String? = null,
    project: Project? = null,
    fileChooserDescriptor: FileChooserDescriptor = FileChooserDescriptorFactory.createSingleFileNoJarsDescriptor(),
    fileChosen: ((chosenFile: VirtualFile) -> String)? = null,
) {
    textFieldWithBrowseButton(
        browseDialogTitle = browseDialogTitle,
        project = project,
        fileChooserDescriptor = fileChooserDescriptor,
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
