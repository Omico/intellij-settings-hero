// Copyright 2023 Omico
// SPDX-License-Identifier: GPL-3.0-only
package me.omico.intellij.settingsHero.utility

import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager

internal inline val progressManager: ProgressManager
    get() = ProgressManager.getInstance()

internal inline val progressIndicator: ProgressIndicator
    get() = progressManager.progressIndicator
