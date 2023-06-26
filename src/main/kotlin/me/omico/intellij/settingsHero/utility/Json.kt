package me.omico.intellij.settingsHero.utility

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

@OptIn(ExperimentalSerializationApi::class)
val prettyJson: Json = Json {
    prettyPrint = true
    prettyPrintIndent = "  "
}
