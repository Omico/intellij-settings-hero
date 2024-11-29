// Copyright 2023-2024 Omico
// SPDX-License-Identifier: GPL-3.0-only
package me.omico.intellij.settingsHero.ui.repository

import com.intellij.openapi.ui.ValidationInfo
import com.intellij.ui.layout.ValidationInfoBuilder
import me.omico.intellij.settingsHero.message
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.pathString

internal fun localRepositoryPathValidationMessage(text: String): String? {
    var message: String? = null
    if (text.isBlank()) return EMPTY_TEXT
    runCatching { Path(text) }
        .onFailure { message = message("settingsHero.textField.repository.validationInfo.invalidLocalPath") }
        .onSuccess {
            message = when {
                it.pathString != it.absolutePathString() ->
                    message("settingsHero.textField.repository.validationInfo.requireAbsoluteLocalPath")
                else -> null
            }
        }
    return message
}

internal fun ValidationInfoBuilder.localRepositoryPathValidationInfo(text: String): ValidationInfo? =
    localRepositoryPathValidationMessage(text)?.ifEmpty { null }?.let(::error)

// TODO Remote
internal fun remoteRepositoryUrlValidationMessage(text: String): String? {
    var message: String? = null
    if (text.isBlank()) return EMPTY_TEXT
    return message
}

internal fun ValidationInfoBuilder.remoteRepositoryUrlValidationInfo(text: String): ValidationInfo? =
    remoteRepositoryUrlValidationMessage(text)?.ifEmpty { null }?.let(::error)

private const val EMPTY_TEXT: String = ""
