// Copyright 2023 Omico
// SPDX-License-Identifier: GPL-3.0-only
package me.omico.intellij.settingsHero.utility

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.nio.file.Path
import kotlin.io.path.readText
import kotlin.io.path.writeText

@OptIn(ExperimentalSerializationApi::class)
internal val prettyJson: Json = Json {
    prettyPrint = true
    prettyPrintIndent = "  "
}

internal inline fun <reified T> Path.decodeAsJson(): T = Json.decodeFromString<T>(readText())

internal inline fun <reified T> Path.encodeAsPrettyJson(value: T): Unit =
    prettyJson.encodeToString(value).let(::writeText)
