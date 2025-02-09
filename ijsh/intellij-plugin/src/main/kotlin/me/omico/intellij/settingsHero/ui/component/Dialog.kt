// Copyright 2023-2024 Omico
// SPDX-License-Identifier: GPL-3.0-only
package me.omico.intellij.settingsHero.ui.component

import com.intellij.openapi.ui.ValidationInfo
import com.intellij.openapi.util.NlsContexts
import com.intellij.ui.components.dialog
import com.intellij.ui.dsl.builder.panel
import java.awt.Dimension

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
                            minimumSize = Dimension(400, minimumSize.height)
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
