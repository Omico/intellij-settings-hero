// Copyright 2024 Omico
// SPDX-License-Identifier: GPL-3.0-only
package me.omico.intellij.settingsHero.idea

import me.omico.intellij.settingsHero.utility.ideaConfigurationDirectory
import java.nio.file.Path

data class IdeaConfigurationFile(
    val name: String,
    val type: IdeaConfigurationFileType = IdeaConfigurationFileType.DEFAULT,
    val platform: IdeaOsPlatform? = null,
) {
    val relativePath: String = when {
        platform != null -> "$type/$platform/$name"
        else -> "$type/$name"
    }
}

fun IdeaConfigurationFile.resolvePath(): Path = ideaConfigurationDirectory.resolve(relativePath)
