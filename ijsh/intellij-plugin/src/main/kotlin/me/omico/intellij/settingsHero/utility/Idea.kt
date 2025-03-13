// Copyright 2023-2025 Omico
// SPDX-License-Identifier: GPL-3.0-only
package me.omico.intellij.settingsHero.utility

import com.intellij.configurationStore.StateStorageManager
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.PathManager
import com.intellij.openapi.components.impl.stores.stateStore
import java.nio.file.Path
import kotlin.io.path.invariantSeparatorsPathString

val ideaConfigurationDirectory: Path = PathManager.getConfigDir()
val ideaConfigurationDirectoryPathString: String = ideaConfigurationDirectory.invariantSeparatorsPathString

fun String.removeIdeaConfigurationDirectoryPrefix(): String =
    removePrefix(ideaConfigurationDirectoryPathString).removePrefix("/")

@Suppress("UnstableApiUsage")
fun storageManager(): StateStorageManager = ApplicationManager.getApplication().stateStore.storageManager
