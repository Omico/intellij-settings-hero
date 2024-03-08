// Copyright 2024 Omico
// SPDX-License-Identifier: GPL-3.0-only
package me.omico.intellij.settingsHero.idea

enum class IdeaOsPlatform(private val value: String) {
    LINUX("linux"),
    MAC("mac"),
    WINDOWS("windows"),
    ;

    override fun toString(): String = value
}
