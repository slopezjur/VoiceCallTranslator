package com.sergiolopez.voicecalltranslator.feature.call.ui

import android.telecom.DisconnectCause
import com.sergiolopez.voicecalltranslator.VoiceCallTranslatorViewModel
import com.sergiolopez.voicecalltranslator.feature.call.domain.model.Call
import com.sergiolopez.voicecalltranslator.feature.call.domain.usecase.GetTelecomCallUseCase
import com.sergiolopez.voicecalltranslator.feature.call.telecom.model.TelecomCall
import com.sergiolopez.voicecalltranslator.feature.call.webrtc.bridge.MainRepository
import com.sergiolopez.voicecalltranslator.feature.common.domain.service.FirebaseAuthService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class CallViewModel @Inject constructor(
    private val getTelecomCallUseCase: GetTelecomCallUseCase,
    //private val webRtcManager: WebRtcManager,
    private val mainRepository: MainRepository,
    private val firebaseAuthService: FirebaseAuthService
) : VoiceCallTranslatorViewModel() {

    private val _callUiState = MutableStateFlow(CallUiState.STARTING)
    val callUiState: StateFlow<CallUiState>
        get() = _callUiState.asStateFlow()


    private val _telecomCallState = MutableStateFlow<TelecomCall>(TelecomCall.None)
    val telecomCallState: StateFlow<TelecomCall>
        get() = _telecomCallState.asStateFlow()


    private val _callState = MutableStateFlow<Call>(Call.CallNoData)
    val callState: StateFlow<Call>
        get() = _callState.asStateFlow()

    fun endCallAndUnregister(): () -> Unit = {
        val telecomCall = _telecomCallState.value
        if (telecomCall is TelecomCall.Registered) {
            _telecomCallState.value = TelecomCall.Unregistered(
                id = telecomCall.id,
                callAttributes = telecomCall.callAttributes,
                disconnectCause = DisconnectCause(DisconnectCause.CANCELED)
            )
            //mainRepository.sendEndCall(calleeId)
        }
    }

    suspend fun subscribeTelecomCallState() {
        getTelecomCallUseCase.invoke().collect {
            _telecomCallState.value = it
        }
    }

    fun sendConnectionRequest(calleeId: String) {
        launchCatching {
            firebaseAuthService.currentUser.collect { user ->
                user?.id?.let { userId ->
                    mainRepository.sendConnectionRequest(
                        sender = userId,
                        target = calleeId,
                        isVideoCall = false
                    )
                }

            }
        }
    }


    fun setCallUiState(callUiState: CallUiState) {
        _callUiState.value = callUiState
    }

    enum class CallUiState {
        STARTING,
        CALLING,
        CALL_IN_PROGRESS,
        ERROR,
        CALL_FINISHED
    }
}