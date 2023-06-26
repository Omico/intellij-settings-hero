package me.omico.intellij.settingsHero.ui.component

import com.intellij.openapi.util.NlsContexts
import com.intellij.ui.dsl.builder.Panel
import com.intellij.ui.dsl.builder.Row

fun Panel.group(
    modifier: Row.() -> Unit = {},
    @NlsContexts.BorderTitle title: String? = null,
    indent: Boolean = true,
    init: Panel.() -> Unit,
) {
    group(
        title = title,
        indent = indent,
        init = init,
    ).apply(modifier)
}
