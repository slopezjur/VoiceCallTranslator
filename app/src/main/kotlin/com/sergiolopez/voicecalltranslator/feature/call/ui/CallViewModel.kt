package com.sergiolopez.voicecalltranslator.feature.call.ui

import com.sergiolopez.voicecalltranslator.VoiceCallTranslatorViewModel
import com.sergiolopez.voicecalltranslator.feature.call.data.network.webrtc.bridge.WebRtcRepository
import com.sergiolopez.voicecalltranslator.feature.call.domain.model.Call
import com.sergiolopez.voicecalltranslator.feature.call.domain.model.CallStatus
import com.sergiolopez.voicecalltranslator.feature.call.domain.model.Message
import com.sergiolopez.voicecalltranslator.feature.call.domain.usecase.GetLastMessageFromCallUseCase
import com.sergiolopez.voicecalltranslator.feature.call.domain.usecase.GetLastTranscriptionMessageUseCase
import com.sergiolopez.voicecalltranslator.feature.call.ui.notification.CallNotificationManager
import com.sergiolopez.voicecalltranslator.feature.common.data.repository.FirebaseAuthRepository
import com.sergiolopez.voicecalltranslator.feature.common.domain.model.Contact
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
    private val firebaseAuthRepository: FirebaseAuthRepository,
    private val callNotificationManager: CallNotificationManager
) : VoiceCallTranslatorViewModel() {

    private val _callState = MutableStateFlow<Call>(Call.CallNoData)
    val callState: StateFlow<Call>
        get() = _callState.asStateFlow()

    private val _callUiStatusState = MutableStateFlow(CallStatus.STARTING)
    val callUiStatusState: StateFlow<CallStatus>
        get() = _callUiStatusState.asStateFlow()

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
                    _callUiStatusState.value = call.callStatus
                    callNotificationManager.updateCallNotification(
                        call = call
                    )
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

    fun sendConnectionRequest(targetContact: Contact) {
        _messageQueueState.value = LinkedList()
        launchCatching {
            webRtcRepository.sendConnectionRequest(
                targetContact = targetContact
            )
        }
    }

    fun answerCall() {
        if (_callState.value is Call.CallData) {
            val callData = _callState.value as Call.CallData
            if (callData.isIncoming && callData.callStatus == CallStatus.INCOMING_CALL) {
                webRtcRepository.setTargetContact(
                    Contact(
                        id = callData.callerId,
                        email = callData.callerEmail
                    )
                )
                webRtcRepository.startCall(callData = callData)
            } else {
                // TODO : This is necessary?
                webRtcRepository.setTargetContact(
                    Contact(
                        id = callData.calleeId,
                        email = callData.calleeEmail
                    )
                )
            }

            //setCallUiState(CallUiState.CALL_IN_PROGRESS)
        }
    }

    fun sendEndCall() {
        _messageQueueState.value = LinkedList()
        if (_callState.value is Call.CallData) {
            val callData = (_callState.value as Call.CallData)
            webRtcRepository.sendEndCall(
                targetContact = if (callData.isIncoming) {
                    Contact(
                        id = callData.callerId,
                        email = callData.callerEmail
                    )
                } else {
                    Contact(
                        id = callData.calleeId,
                        email = callData.calleeEmail
                    )
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