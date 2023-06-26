package me.omico.intellij.settingsHero.utility

import com.intellij.openapi.application.PathManager
import java.nio.file.Path
import kotlin.io.path.invariantSeparatorsPathString

val ideaConfigurationDirectory: Path = PathManager.getConfigDir()
val ideaConfigurationDirectoryPathString: String = ideaConfigurationDirectory.invariantSeparatorsPathString

fun String.removeIdeaConfigurationDirectoryPrefix(): String =
    removePrefix(ideaConfigurationDirectoryPathString).removePrefix("/")
