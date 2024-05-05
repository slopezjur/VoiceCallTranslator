package com.sergiolopez.voicecalltranslator.feature.call.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class CallAction {
    Answer,
    ToggleMute,
    Disconnect
}