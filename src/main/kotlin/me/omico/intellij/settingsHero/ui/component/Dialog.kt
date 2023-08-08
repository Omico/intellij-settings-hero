// Copyright 2023 Omico
// SPDX-License-Identifier: GPL-3.0-only
package me.omico.intellij.settingsHero.ui.component

import com.intellij.openapi.ui.ValidationInfo
import com.intellij.openapi.util.NlsContexts
import com.intellij.ui.components.dialog
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.util.minimumWidth

fun showTextFieldDialog(
    @NlsContexts.DialogTitle title: String,
    initialText: String? = null,
    ok: (text: String?) -> List<ValidationInfo>,
) {
    var value: String? = initialText
    dialog(
        title = title,
        panel = panel {
            row {
                textField(
                    modifier = {
                        focused()
                        applyToComponent {
                            minimumWidth = 400
                            text = initialText
                        }
                        onChanged { textField -> value = textField.text }
                    },
                )
            }
        },
        ok = { ok(value) },
    ).show()
}
