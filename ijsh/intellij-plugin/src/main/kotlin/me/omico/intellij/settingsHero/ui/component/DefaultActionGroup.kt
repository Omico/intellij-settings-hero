// Copyright 2023-2024 Omico
// SPDX-License-Identifier: GPL-3.0-only
package me.omico.intellij.settingsHero.ui.component

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.util.NlsActions
import javax.swing.Icon

fun DefaultActionGroup.addDumbAwareAction(
    @NlsActions.ActionText text: String? = null,
    icon: Icon? = null,
    actionPerformed: AnActionEvent.() -> Unit,
) {
    DumbAwareAction.create(text, icon, actionPerformed).let(::add)
}
