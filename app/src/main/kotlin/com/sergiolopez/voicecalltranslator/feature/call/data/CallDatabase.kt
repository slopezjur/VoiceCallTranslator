package com.sergiolopez.voicecalltranslator.feature.call.data

import com.sergiolopez.voicecalltranslator.feature.call.domain.model.CallStatus

data class CallDatabase(
    val callerId: String? = null,
    val calleeId: String? = null,
    val offerData: String? = null,
    val isIncoming: Boolean? = null,
    val callStatus: CallStatus? = null,
    val language: String,
    val timestamp: Long? = null
)