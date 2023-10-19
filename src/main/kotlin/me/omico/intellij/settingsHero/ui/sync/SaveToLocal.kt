// Copyright 2023 Omico
// SPDX-License-Identifier: GPL-3.0-only
package me.omico.intellij.settingsHero.ui.sync

import com.intellij.openapi.observable.properties.GraphProperty
import com.intellij.openapi.progress.ProgressManager
import com.intellij.ui.JBColor
import com.intellij.ui.dsl.builder.Panel
import com.intellij.ui.dsl.builder.bindText
import me.omico.intellij.settingsHero.message
import me.omico.intellij.settingsHero.profile.saveTo
import me.omico.intellij.settingsHero.ui.component.button
import me.omico.intellij.settingsHero.ui.component.label
import me.omico.intellij.settingsHero.ui.currentProfileProperty
import me.omico.intellij.settingsHero.ui.isSettingsHeroEnabledProperty
import me.omico.intellij.settingsHero.ui.localRepositoryDirectoryProperty
import me.omico.intellij.settingsHero.ui.propertyGraph
import org.jetbrains.concurrency.runAsync
import kotlin.io.path.Path

private val saveToLocalMessage: GraphProperty<String> = propertyGraph.property("")

internal fun Panel.saveToLocal() {
    saveToLocalMessage.afterChange {
        if (it.isEmpty()) return@afterChange
        runAsync {
            Thread.sleep(3000)
            saveToLocalMessage.set("")
        }
    }
    row {
        button(
            modifier = { visibleIf(isSettingsHeroEnabledProperty) },
            text = message("settingsHero.button.saveToLocal"),
            onClick = {
                ProgressManager.getInstance().executeNonCancelableSection {
                    localRepositoryDirectoryProperty.get().let {
                        if (it.isEmpty()) return@let
                        currentProfileProperty.get()?.run {
                            saveTo(Path(it))
                            saveToLocalMessage.set(message("settingsHero.message.saveToLocalSuccess"))
                        }
                    }
                }
            },
        )
        label(
            modifier = {
                applyToComponent {
                    foreground = JBColor.GREEN
                }
                bindText(saveToLocalMessage)
            },
        )
    }
}
