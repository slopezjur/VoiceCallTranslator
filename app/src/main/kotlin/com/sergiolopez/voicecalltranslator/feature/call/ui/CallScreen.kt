package com.sergiolopez.voicecalltranslator.feature.call.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sergiolopez.voicecalltranslator.R
import com.sergiolopez.voicecalltranslator.feature.call.domain.model.Call
import com.sergiolopez.voicecalltranslator.feature.call.domain.model.CallAction
import com.sergiolopez.voicecalltranslator.feature.call.domain.model.CallStatus
import com.sergiolopez.voicecalltranslator.feature.call.domain.model.Message
import com.sergiolopez.voicecalltranslator.feature.common.domain.model.Contact
import com.sergiolopez.voicecalltranslator.feature.common.utils.Dummy
import com.sergiolopez.voicecalltranslator.navigation.CALLEE_DEFAULT_ID
import com.sergiolopez.voicecalltranslator.navigation.NavigationParams
import com.sergiolopez.voicecalltranslator.navigation.NavigationRoute
import com.sergiolopez.voicecalltranslator.theme.VoiceCallTranslatorPreview
import kotlinx.coroutines.delay

@Composable
fun CallScreen(
    navigateAndPopUp: (NavigationParams) -> Unit,
    calleeId: String,
    calleeEmail: String,
    callViewModel: CallViewModel = hiltViewModel()
) {
    val call = callViewModel.callState.collectAsStateWithLifecycle().value

    val callUiState = callViewModel.callUiStatusState.collectAsStateWithLifecycle().value

    val messageQueueState = callViewModel.messageQueueState.collectAsStateWithLifecycle().value

    val sendConnectionRequest: (Contact) -> Unit = {
        callViewModel.sendConnectionRequest(it)
    }

    val answerCall: () -> Unit = {
        callViewModel.answerCall()
    }

    val sendEndCall: () -> Unit = {
        callViewModel.sendEndCall()
    }

    val shouldBeMuted: (Boolean) -> Unit = {
        callViewModel.shouldBeMuted(
            shouldBeMuted = it
        )
    }

    val shouldBeSpeaker: (Boolean) -> Unit = {
        callViewModel.shouldBeSpeaker(
            shouldBeSpeaker = it
        )
    }

    CallScreenContent(
        navigateAndPopUp = navigateAndPopUp,
        callUiState = callUiState,
        calleeId = calleeId,
        calleeEmail = calleeEmail,
        call = call,
        sendConnectionRequest = sendConnectionRequest,
        answerCall = answerCall,
        sendEndCall = sendEndCall,
        shouldBeMuted = shouldBeMuted,
        shouldBeSpeaker = shouldBeSpeaker,
        messages = messageQueueState.toList()
    )
}

@Composable
fun CallScreenContent(
    modifier: Modifier = Modifier,
    navigateAndPopUp: (NavigationParams) -> Unit,
    callUiState: CallStatus,
    calleeId: String,
    calleeEmail: String,
    call: Call,
    sendConnectionRequest: (Contact) -> Unit,
    answerCall: () -> Unit,
    sendEndCall: () -> Unit,
    shouldBeMuted: (Boolean) -> Unit,
    shouldBeSpeaker: (Boolean) -> Unit,
    messages: List<Message>
) {
    when (callUiState) {
        CallStatus.STARTING -> when (call) {
            // TODO : Unnecessary now?
            is Call.CallData -> {
                if (call.callStatus != CallStatus.CALL_IN_PROGRESS && !call.isIncoming) {
                    sendConnectionRequest.invoke(
                        Contact(
                            id = call.calleeId,
                            email = call.calleeEmail
                        )
                    )
                }
            }

            else -> {
                if (calleeId != CALLEE_DEFAULT_ID) {
                    sendConnectionRequest.invoke(
                        Contact(
                            id = calleeId,
                            email = calleeEmail
                        )
                    )
                }
            }
        }

        CallStatus.CALL_FINISHED -> {
            LaunchedEffect(Unit) {
                delay(1500)
                navigateToContactList(navigateAndPopUp)
            }
            NoCallScreen(
                modifier = modifier
            )
        }

        else -> {
            CallScreenDetails(
                modifier = modifier,
                callUiState = callUiState,
                call = call,
                onCallAction = {
                    when (it) {
                        is CallAction.Answer -> {
                            answerCall.invoke()
                        }

                        is CallAction.ToggleMute -> {
                            shouldBeMuted.invoke(it.isMuted)
                        }

                        is CallAction.ToggleSpeaker -> {
                            shouldBeSpeaker.invoke(it.isSpeaker)
                        }

                        is CallAction.Disconnect -> {
                            sendEndCall.invoke()
                        }
                    }
                },
                messages = messages
            )
        }
    }
}

private fun navigateToContactList(navigateAndPopUp: (NavigationParams) -> Unit) {
    navigateAndPopUp.invoke(
        NavigationParams(
            NavigationRoute.CONTACT_LIST.navigationName,
            NavigationRoute.CALL.navigationName
        )
    )
}

@Composable
private fun NoCallScreen(modifier: Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(id = R.string.call_ended),
            style = MaterialTheme.typography.titleLarge
        )
    }
}

@PreviewLightDark
@Composable
fun CallScreenPreview() {
    VoiceCallTranslatorPreview {
        CallScreenContent(
            navigateAndPopUp = {},
            callUiState = CallStatus.CALL_IN_PROGRESS,
            calleeId = CALLEE_DEFAULT_ID,
            calleeEmail = "test@test.com",
            call = Call.CallNoData,
            sendConnectionRequest = {},
            answerCall = {},
            sendEndCall = {},
            shouldBeMuted = {},
            shouldBeSpeaker = {},
            messages = Dummy.messages
        )
    }
}