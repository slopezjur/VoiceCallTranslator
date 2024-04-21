package com.sergiolopez.voicecalltranslator.feature.common.domain.subscriber

import com.sergiolopez.voicecalltranslator.feature.call.domain.model.Call
import com.sergiolopez.voicecalltranslator.feature.common.data.FirebaseDatabaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrentCallSubscriber @Inject constructor(
    val firebaseDatabaseRepository: FirebaseDatabaseRepository
) {

    private val _currentCallState = MutableStateFlow<List<Call>>(listOf(Call.CallNoData))
    val currentCallState: StateFlow<List<Call>> = _currentCallState

    suspend fun subscribe() {
        val result = firebaseDatabaseRepository.getCallList()
        if (result.isSuccess) {
            result.getOrNull()?.collect {
                _currentCallState.value = it
            }
        }
    }
}