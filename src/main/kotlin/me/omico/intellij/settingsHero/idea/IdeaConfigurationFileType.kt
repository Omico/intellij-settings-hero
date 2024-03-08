// Copyright 2024 Omico
// SPDX-License-Identifier: GPL-3.0-only
package me.omico.intellij.settingsHero.idea

enum class IdeaConfigurationFileType(private val folderName: String) {
    DEFAULT(""),
    CODE_STYLE("codestyles"),
    COLOR_SCHEME("colors"),
    KEYMAP("keymaps"),
    OPTION("options"),
    ;

    override fun toString(): String = folderName
}
