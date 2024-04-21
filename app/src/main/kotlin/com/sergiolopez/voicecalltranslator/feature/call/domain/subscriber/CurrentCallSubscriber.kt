package com.sergiolopez.voicecalltranslator.feature.call.domain.subscriber

import com.sergiolopez.voicecalltranslator.feature.call.telecom.model.TelecomCall
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrentCallSubscriber @Inject constructor() {

    private val _currentCallState = MutableStateFlow<TelecomCall>(TelecomCall.None)
    val currentCallState: StateFlow<TelecomCall> = _currentCallState

    fun updateCurrentCallState(telecomCall: TelecomCall) {
        _currentCallState.value = telecomCall
    }
}