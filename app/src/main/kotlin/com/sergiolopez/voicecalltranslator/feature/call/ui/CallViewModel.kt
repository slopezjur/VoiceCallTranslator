package com.sergiolopez.voicecalltranslator.feature.call.ui

import com.sergiolopez.voicecalltranslator.VoiceCallTranslatorViewModel
import com.sergiolopez.voicecalltranslator.feature.call.domain.subscriber.CurrentCallSubscriber
import com.sergiolopez.voicecalltranslator.feature.call.domain.usecase.CreateCallUseCase
import com.sergiolopez.voicecalltranslator.feature.common.domain.subscriber.CurrentUserSubscriber
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class CallViewModel @Inject constructor(
    private val currentUserSubscriber: CurrentUserSubscriber,
    private val createCallUseCase: CreateCallUseCase,
    private val currentCallSubscriber: CurrentCallSubscriber
) : VoiceCallTranslatorViewModel() {

    private val _callUiState = MutableStateFlow(CallUiState.STARTING)
    val callUiState: StateFlow<CallUiState> = _callUiState

    fun startCall(calleeId: String) {
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
}