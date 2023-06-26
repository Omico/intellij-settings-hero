package me.omico.intellij.settingsHero.utility

fun <T> MutableCollection<in T>.clearAndAddAll(elements: Iterable<T>) {
    clear()
    addAll(elements)
}
