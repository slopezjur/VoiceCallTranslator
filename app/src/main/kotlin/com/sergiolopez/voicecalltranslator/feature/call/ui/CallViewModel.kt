package com.sergiolopez.voicecalltranslator.feature.call.ui

import com.sergiolopez.voicecalltranslator.VoiceCallTranslatorViewModel
import com.sergiolopez.voicecalltranslator.feature.call.domain.model.Call
import com.sergiolopez.voicecalltranslator.feature.call.domain.model.CallStatus
import com.sergiolopez.voicecalltranslator.feature.call.webrtc.bridge.MainRepository
import com.sergiolopez.voicecalltranslator.feature.common.domain.service.FirebaseAuthService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class CallViewModel @Inject constructor(
    //private val webRtcManager: WebRtcManager,
    private val mainRepository: MainRepository,
    private val firebaseAuthService: FirebaseAuthService
) : VoiceCallTranslatorViewModel() {

    private val _callUiState = MutableStateFlow(CallUiState.STARTING)
    val callUiState: StateFlow<CallUiState>
        get() = _callUiState.asStateFlow()


    private val _callState = MutableStateFlow<Call>(Call.CallNoData)
    val callState: StateFlow<Call>
        get() = _callState.asStateFlow()

    init {
        subscribeCallState()
    }

    private fun subscribeCallState() {
        launchCatching {
            mainRepository.currentCall.collect {
                _callState.value = it
            }
        }
    }

    fun sendConnectionRequest(calleeId: String) {
        launchCatching {
            mainRepository.sendConnectionRequest(
                target = calleeId
            )
        }
        setCallUiState(CallUiState.CALLING)
    }

    fun answerCall() {
        if (_callState.value is Call.CallData) {
            val callData = _callState.value as Call.CallData
            if (callData.isIncoming && callData.callStatus == CallStatus.INCOMING_CALL) {
                mainRepository.setTarget(callData.callerId)
                mainRepository.startCall(callData = callData)
            } else {
                // TODO : This is necessary?
                mainRepository.setTarget(callData.calleeId)
            }

            setCallUiState(CallUiState.CALL_IN_PROGRESS)
        }
    }

    fun sendEndCall() {
        if (callState.value is Call.CallData) {
            mainRepository.sendEndCall(
                target = (callState.value as Call.CallData).calleeId
            )
        }

        setCallUiState(CallUiState.CALL_FINISHED)
    }

    fun setCallUiState(callUiState: CallUiState) {
        _callUiState.value = callUiState
    }

    enum class CallUiState {
        STARTING,
        CALLING,
        INCOMING_CALL,
        ANSWERING,
        CALL_IN_PROGRESS,
        ERROR,
        FINISHING_CALL,
        CALL_FINISHED
    }
}