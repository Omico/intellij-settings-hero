// Copyright 2023-2024 Omico
// SPDX-License-Identifier: GPL-3.0-only
@file:Suppress("UnstableApiUsage")

package me.omico.intellij.settingsHero.utility

import com.intellij.ide.lightEdit.LightEditService
import com.intellij.openapi.components.service

inline val lightEditService: LightEditService
    get() = service()
