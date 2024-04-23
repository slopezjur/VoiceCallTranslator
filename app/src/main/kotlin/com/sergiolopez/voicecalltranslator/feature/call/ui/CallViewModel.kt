package com.sergiolopez.voicecalltranslator.feature.call.ui

import com.sergiolopez.voicecalltranslator.VoiceCallTranslatorViewModel
import com.sergiolopez.voicecalltranslator.feature.call.domain.model.Call
import com.sergiolopez.voicecalltranslator.feature.call.domain.model.CallStatus
import com.sergiolopez.voicecalltranslator.feature.call.domain.usecase.CreateCallUseCase
import com.sergiolopez.voicecalltranslator.feature.call.domain.usecase.GetTelecomCallUseCase
import com.sergiolopez.voicecalltranslator.feature.call.telecom.model.TelecomCall
import com.sergiolopez.voicecalltranslator.feature.call.webrtc.DataModel
import com.sergiolopez.voicecalltranslator.feature.call.webrtc.WebRTCClient
import com.sergiolopez.voicecalltranslator.feature.common.domain.service.FirebaseAuthService
import com.sergiolopez.voicecalltranslator.feature.common.domain.subscriber.CurrentCallSubscriber
import com.sergiolopez.voicecalltranslator.feature.contactlist.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class CallViewModel @Inject constructor(
    private val createCallUseCase: CreateCallUseCase,
    private val currentCallSubscriber: CurrentCallSubscriber,
    private val webRTCClient: WebRTCClient,
    private val getTelecomCallUseCase: GetTelecomCallUseCase,
    private val firebaseAuthService: FirebaseAuthService
) : VoiceCallTranslatorViewModel(), WebRTCClient.Listener {

    private val _callUiState = MutableStateFlow(CallUiState.STARTING)
    val callUiState: StateFlow<CallUiState> = _callUiState

    private val _telecomCallState = MutableStateFlow<TelecomCall>(TelecomCall.None)
    val telecomCallState: StateFlow<TelecomCall> = _telecomCallState

    suspend fun subscribeTelecomCallState() {
        getTelecomCallUseCase.invoke().collect {
            _telecomCallState.value = it
        }
    }

    fun startCall(calleeId: String) {
        webRTCClient.listener = this
        webRTCClient.call(calleeId)


        /*launchCatching {
            processCallActions(actionSource.consumeAsFlow())
        }

        currentCallSubscriber.updateCurrentCallState(
            TelecomCall.Registered(
                isActive = false,
                isOnHold = false,
                call = Call(
                    callerId = currentUserSubscriber.currentUserState.value?.id ?: "",
                    calleeId = calleeId,
                    offerData = null,
                    answerData = null,
                    isIncoming = false,
                    callStatus = CallStatus.CALL_IN_PROGRESS
                ),
                isMuted = false,
                errorCode = null,
                actionSource = actionSource
            )
        )*/
    }

    fun setCallUiState(callUiState: CallUiState) {
        _callUiState.value = callUiState
    }

    enum class CallUiState {
        STARTING,
        CALLING,
        CALL_IN_PROGRESS,
        ERROR
    }

    override fun onTransferEventToSocket(data: DataModel) {
        launchCatching {
            firebaseAuthService.currentUser.collect { user ->
                if (user is User.UserData) {
                    launchCatching {
                        createCallUseCase.invoke(
                            Call.CallData(
                                callerId = user.id,
                                calleeId = data.target,
                                offerData = data.data,
                                answerData = "",
                                isIncoming = false,
                                callStatus = CallStatus.CALLING
                            )
                        )
                    }
                }
            }
        }
    }
}