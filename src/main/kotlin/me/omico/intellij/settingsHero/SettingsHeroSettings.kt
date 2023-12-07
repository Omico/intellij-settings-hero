// Copyright 2023 Omico
// SPDX-License-Identifier: GPL-3.0-only
package me.omico.intellij.settingsHero

import com.intellij.openapi.components.BaseState
import com.intellij.openapi.components.RoamingType
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.SimplePersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.service
import kotlin.io.path.Path
import kotlin.io.path.exists

internal val settingsHeroSettings: SettingsHeroSettings = service<SettingsHeroSettings>()

@Service(Service.Level.APP)
@State(
    name = "SettingsHeroSettings",
    storages = [Storage(value = "settingsHero.xml")],
)
internal class SettingsHeroSettings : SimplePersistentStateComponent<SettingsHeroSettings.State>(State()) {
    private val localSettingsHeroSettings: LocalSettingsHeroSettings = service<LocalSettingsHeroSettings>()

    var enabled: Boolean by localSettingsHeroSettings.state::enabled

    var repositoryType: SettingsHeroRepositoryType
        get() = runCatching { SettingsHeroRepositoryType.valueOf(state.repositoryType!!) }
            .getOrElse {
                repositoryType = SettingsHeroRepositoryType.LOCAL
                SettingsHeroRepositoryType.LOCAL
            }
        set(value) {
            state.repositoryType = value.name.ifBlank { null }
        }

    var remoteRepositoryUrl: String
        get() = state.remoteRepositoryUrl ?: ""
        set(value) {
            state.remoteRepositoryUrl = value.ifBlank { null }
        }

    var localRepositoryDirectory: String
        get() = localSettingsHeroSettings.state.localRepository
            ?.takeIf { directory -> Path(directory).exists() }
            ?: ""
        set(value) {
            localSettingsHeroSettings.state.localRepository = value.ifBlank { null }
        }

    var currentProfile: String?
        get() = localSettingsHeroSettings.state.currentProfile
        set(value) {
            localSettingsHeroSettings.state.currentProfile = value?.ifBlank { null }
        }

    class State : BaseState() {
        var repositoryType: String? by string()
        var remoteRepositoryUrl: String? by string()
    }
}

@Service(Service.Level.APP)
@State(
    name = "LocalSettingsHeroSettings",
    storages = [
        Storage(
            value = "settingsHero.local.xml",
            roamingType = RoamingType.DISABLED,
        ),
    ],
)
private class LocalSettingsHeroSettings : SimplePersistentStateComponent<LocalSettingsHeroSettings.State>(State()) {
    class State : BaseState() {
        var enabled: Boolean by property(defaultValue = false)
        var localRepository: String? by string()
        var currentProfile: String? by string()
    }
}
