@file:Suppress("UnstableApiUsage")

package me.omico.intellij.settingsHero.utility

import com.intellij.ide.lightEdit.LightEditService
import com.intellij.openapi.components.service

val lightEditService: LightEditService = service()
