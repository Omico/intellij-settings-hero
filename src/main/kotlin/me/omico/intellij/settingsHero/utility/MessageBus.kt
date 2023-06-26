package me.omico.intellij.settingsHero.utility

import com.intellij.openapi.components.service
import com.intellij.util.messages.MessageBus

val messageBus: MessageBus = service<MessageBus>()
