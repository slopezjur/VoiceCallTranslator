package com.sergiolopez.voicecalltranslator.feature.call.domain.model

data class Message(
    val text: String,
    val timestamp: Long,
    val isSent: Boolean
)