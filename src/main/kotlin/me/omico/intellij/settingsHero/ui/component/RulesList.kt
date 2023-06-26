package me.omico.intellij.settingsHero.ui.component

import com.intellij.CommonBundle
import com.intellij.openapi.observable.properties.ObservableMutableProperty
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.openapi.util.NlsContexts
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.components.JBList
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.LabelPosition
import com.intellij.ui.dsl.builder.Row
import javax.swing.DefaultListModel

fun Row.rulesList(
    @NlsContexts.Label label: String,
    rulesProperty: ObservableMutableProperty<Set<String>>,
) {
    val model = RulesListModel()
    model.addAll(rulesProperty.get())
    rulesProperty.afterChange(model::reload)
    RulesList(model)
        .createDecorator()
        .createPanel()
        .let(::cell)
        .label(label = label, position = LabelPosition.TOP)
        .onIsModified { rulesProperty.get() != model.items }
        .onApply { rulesProperty.set(model.items) }
        .onReset { model.reload(rulesProperty.get()) }
        .align(Align.FILL)
        .resizableColumn()
}

private class RulesList(
    private val model: RulesListModel,
) : JBList<String>(model) {
    fun newRule(): Unit =
        showTextFieldDialog(
            title = "Add Rule",
            ok = { text ->
                buildList {
                    when (text) {
                        null -> ValidationInfo("Rule cannot be empty").let(::add)
                        else -> model.addElement(text)
                    }
                }
            },
        )

    fun editRule(): Unit =
        showTextFieldDialog(
            title = "Edit Rule",
            initialText = selectedValue,
            ok = { text ->
                buildList {
                    when (text) {
                        null -> ValidationInfo("Rule cannot be empty").let(::add)
                        else -> model.setElementAt(text, selectedIndex)
                    }
                }
            },
        )

    fun remove(): Unit = model.removeElementAt(selectedIndex)
}

private class RulesListModel : DefaultListModel<String>() {
    val items: Set<String>
        get() = elements().toList().toSortedSet()

    fun reload(rules: Set<String>) {
        clear()
        addAll(rules)
    }
}

private fun RulesList.createDecorator(): ToolbarDecorator =
    ToolbarDecorator
        .createDecorator(this)
        .disableUpDownActions()
        .setAddAction { newRule() }
        .setEditAction { editRule() }
        .setRemoveAction { remove() }
        .setButtonComparator(
            CommonBundle.message("button.add"),
            CommonBundle.message("button.edit"),
            CommonBundle.message("button.remove"),
        )
