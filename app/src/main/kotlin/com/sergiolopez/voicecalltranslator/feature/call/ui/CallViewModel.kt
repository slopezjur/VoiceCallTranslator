package com.sergiolopez.voicecalltranslator.feature.call.ui

import com.sergiolopez.voicecalltranslator.VoiceCallTranslatorViewModel
import com.sergiolopez.voicecalltranslator.feature.call.domain.model.Call
import com.sergiolopez.voicecalltranslator.feature.call.domain.model.CallStatus
import com.sergiolopez.voicecalltranslator.feature.call.webrtc.bridge.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class CallViewModel @Inject constructor(
    //private val webRtcManager: WebRtcManager,
    private val mainRepository: MainRepository
) : VoiceCallTranslatorViewModel() {

    private val _callState = MutableStateFlow<Call>(Call.CallNoData)
    val callState: StateFlow<Call>
        get() = _callState.asStateFlow()

    private val _callStatusState = MutableStateFlow(CallStatus.STARTING)
    val callStatusState: StateFlow<CallStatus>
        get() = _callStatusState.asStateFlow()

    init {
        subscribeCallState()
    }

    private fun subscribeCallState() {
        launchCatching {
            mainRepository.currentCall.collect { call ->
                _callState.value = call

                if (call is Call.CallData) {
                    _callStatusState.value = call.callStatus
                }
            }
        }
    }

    fun sendConnectionRequest(calleeId: String) {
        launchCatching {
            mainRepository.sendConnectionRequest(
                target = calleeId
            )
        }
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

            //setCallUiState(CallUiState.CALL_IN_PROGRESS)
        }
    }

    fun sendEndCall() {
        if (_callState.value is Call.CallData) {
            val callData = (_callState.value as Call.CallData)
            mainRepository.sendEndCall(
                target = if (callData.isIncoming) {
                    callData.callerId
                } else {
                    callData.calleeId
                }
            )
        }
    }

    fun shouldBeMuted(shouldBeMuted: Boolean) {
        mainRepository.toggleAudio(shouldBeMuted = shouldBeMuted)
    }
}