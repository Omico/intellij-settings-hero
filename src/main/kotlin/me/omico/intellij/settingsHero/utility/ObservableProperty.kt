package me.omico.intellij.settingsHero.utility

import com.intellij.openapi.Disposable
import com.intellij.openapi.observable.properties.ObservableProperty

fun <T, Property : ObservableProperty<T>> Property.onChanged(
    parentDisposable: Disposable? = null,
    listener: (T) -> Unit,
): Property =
    apply { afterChange(parentDisposable, listener) }
