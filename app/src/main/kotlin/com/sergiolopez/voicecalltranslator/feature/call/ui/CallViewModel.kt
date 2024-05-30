package com.sergiolopez.voicecalltranslator.feature.call.ui

import com.sergiolopez.voicecalltranslator.VoiceCallTranslatorViewModel
import com.sergiolopez.voicecalltranslator.feature.call.data.network.webrtc.bridge.WebRtcRepository
import com.sergiolopez.voicecalltranslator.feature.call.domain.model.Call
import com.sergiolopez.voicecalltranslator.feature.call.domain.model.CallStatus
import com.sergiolopez.voicecalltranslator.feature.call.domain.model.Message
import com.sergiolopez.voicecalltranslator.feature.call.domain.usecase.GetLastMessageFromCallUseCase
import com.sergiolopez.voicecalltranslator.feature.call.domain.usecase.GetLastTranscriptionMessageUseCase
import com.sergiolopez.voicecalltranslator.feature.common.data.repository.FirebaseAuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.LinkedList
import java.util.Queue
import javax.inject.Inject

@HiltViewModel
class CallViewModel @Inject constructor(
    private val webRtcRepository: WebRtcRepository,
    private val getLastTranscriptionMessageUseCase: GetLastTranscriptionMessageUseCase,
    private val getLastMessageFromCallUseCase: GetLastMessageFromCallUseCase,
    private val firebaseAuthRepository: FirebaseAuthRepository
) : VoiceCallTranslatorViewModel() {

    private val _callState = MutableStateFlow<Call>(Call.CallNoData)
    val callState: StateFlow<Call>
        get() = _callState.asStateFlow()

    private val _callStatusState = MutableStateFlow(CallStatus.STARTING)
    val callStatusState: StateFlow<CallStatus>
        get() = _callStatusState.asStateFlow()

    private val _messageQueueState = MutableStateFlow(LinkedList<Message>())
    val messageQueueState: StateFlow<Queue<Message>>
        get() = _messageQueueState.asStateFlow()

    init {
        subscribeToCallState()
        subscribeToTranscriptionState()
        subscribeToTranslationFromCallState()
    }

    private fun subscribeToCallState() {
        launchCatching {
            webRtcRepository.currentCall.collect { call ->
                _callState.value = call

                if (call is Call.CallData) {
                    _callStatusState.value = call.callStatus
                }
            }
        }
    }

    private fun subscribeToTranscriptionState() {
        launchCatching {
            getLastTranscriptionMessageUseCase.invoke().collect { message ->
                updateQueueStatus(message)
            }
        }
    }

    private fun subscribeToTranslationFromCallState() {
        launchCatching {
            firebaseAuthRepository.currentUser.value?.id?.let { userId ->
                getLastMessageFromCallUseCase.invoke(
                    userId
                ).onSuccess {
                    it.collect { message ->
                        updateQueueStatus(message)
                    }
                }
            }
        }
    }

    private fun updateQueueStatus(message: Message) {
        val updatedQueue = LinkedList(_messageQueueState.value).apply {
            add(message)
            // NOTE: Add limit to control resources?
            if (size > CHAT_MESSAGE_HISTORY_LIMIT) {
                pop()
            }
        }
        _messageQueueState.value = updatedQueue
    }

    fun sendConnectionRequest(calleeId: String) {
        launchCatching {
            webRtcRepository.sendConnectionRequest(
                target = calleeId
            )
        }
    }

    fun answerCall() {
        if (_callState.value is Call.CallData) {
            val callData = _callState.value as Call.CallData
            if (callData.isIncoming && callData.callStatus == CallStatus.INCOMING_CALL) {
                webRtcRepository.setTarget(callData.callerId)
                webRtcRepository.startCall(callData = callData)
            } else {
                // TODO : This is necessary?
                webRtcRepository.setTarget(callData.calleeId)
            }

            //setCallUiState(CallUiState.CALL_IN_PROGRESS)
        }
    }

    fun sendEndCall() {
        _messageQueueState.value = LinkedList()
        if (_callState.value is Call.CallData) {
            val callData = (_callState.value as Call.CallData)
            webRtcRepository.sendEndCall(
                target = if (callData.isIncoming) {
                    callData.callerId
                } else {
                    callData.calleeId
                }
            )
        }
    }

    fun shouldBeMuted(shouldBeMuted: Boolean) {
        webRtcRepository.toggleAudio(shouldBeMuted = shouldBeMuted)
    }

    fun shouldBeSpeaker(shouldBeSpeaker: Boolean) {
        webRtcRepository.toggleSpeaker(shouldBeSpeaker = shouldBeSpeaker)
    }

    companion object {
        private const val CHAT_MESSAGE_HISTORY_LIMIT = 500
    }
}