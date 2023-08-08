// Copyright 2023 Omico
// SPDX-License-Identifier: GPL-3.0-only
package me.omico.intellij.settingsHero.utility

fun <T> MutableCollection<in T>.clearAndAddAll(elements: Iterable<T>) {
    clear()
    addAll(elements)
}
