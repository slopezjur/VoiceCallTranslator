package com.sergiolopez.voicecalltranslator.feature.common.domain.subscriber

import com.sergiolopez.voicecalltranslator.feature.call.telecom.model.TelecomCall
import com.sergiolopez.voicecalltranslator.feature.common.data.FirebaseDatabaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrentCallSubscriber @Inject constructor(
    val firebaseDatabaseRepository: FirebaseDatabaseRepository
) {

    private val _currentCallState = MutableStateFlow<TelecomCall>(TelecomCall.None)
    val currentCallState: StateFlow<TelecomCall> = _currentCallState

    fun updateCurrentCallState(telecomCall: TelecomCall) {
        _currentCallState.value = telecomCall
    }
}