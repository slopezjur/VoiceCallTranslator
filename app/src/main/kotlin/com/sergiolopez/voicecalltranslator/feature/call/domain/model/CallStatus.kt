package com.sergiolopez.voicecalltranslator.feature.call.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class CallStatus {
    CALLING,
    INCOMING_CALL,
    CALL_IN_PROGRESS,
    CALL_FINISHED
}
