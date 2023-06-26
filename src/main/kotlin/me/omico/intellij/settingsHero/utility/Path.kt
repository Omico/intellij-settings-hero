package me.omico.intellij.settingsHero.utility

import java.nio.file.Path
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.createDirectory
import kotlin.io.path.deleteRecursively

@OptIn(ExperimentalPathApi::class)
fun Path.clearDirectory() {
    deleteRecursively()
    createDirectory()
}
