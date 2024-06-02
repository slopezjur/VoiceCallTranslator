package com.sergiolopez.voicecalltranslator.feature.call.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class CallStatus {
    STARTING,
    CALLING,
    INCOMING_CALL,
    ANSWERING,
    CALL_IN_PROGRESS,
    RECONNECTING,
    CALL_FINISHED
}
