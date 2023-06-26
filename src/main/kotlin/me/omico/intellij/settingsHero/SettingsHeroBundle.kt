package me.omico.intellij.settingsHero

import com.intellij.DynamicBundle
import org.jetbrains.annotations.ApiStatus
import org.jetbrains.annotations.Nls
import org.jetbrains.annotations.PropertyKey

fun message(
    @PropertyKey(resourceBundle = PATH_TO_BUNDLE) key: String,
    vararg params: Any,
): @Nls String =
    SettingsHeroBundle.message(key = key, params = params)

private const val PATH_TO_BUNDLE = "messages.SettingsHeroBundle"

@ApiStatus.Internal
private object SettingsHeroBundle : DynamicBundle(PATH_TO_BUNDLE) {
    fun message(
        @PropertyKey(resourceBundle = PATH_TO_BUNDLE) key: String,
        vararg params: Any,
    ): @Nls String =
        getMessage(key, *params)
}
