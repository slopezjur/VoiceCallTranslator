package com.sergiolopez.voicecalltranslator.feature.call.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sergiolopez.voicecalltranslator.feature.call.domain.model.Call
import com.sergiolopez.voicecalltranslator.feature.call.domain.model.CallAction
import com.sergiolopez.voicecalltranslator.feature.call.domain.model.CallStatus
import com.sergiolopez.voicecalltranslator.navigation.CALLEE_DEFAULT_ID
import com.sergiolopez.voicecalltranslator.navigation.NavigationCallExtra
import com.sergiolopez.voicecalltranslator.navigation.NavigationParams
import com.sergiolopez.voicecalltranslator.navigation.NavigationRoute
import com.sergiolopez.voicecalltranslator.theme.VoiceCallTranslatorPreview
import kotlinx.coroutines.delay

@Composable
fun CallScreen(
    navigateAndPopUp: (NavigationParams) -> Unit,
    calleeId: String,
    navigationCallExtra: NavigationCallExtra,
    restartFirebaseService: () -> Unit,
    callViewModel: CallViewModel = hiltViewModel()
) {
    val call = callViewModel.callState.collectAsStateWithLifecycle().value

    val callUiState = callViewModel.callStatusState.collectAsStateWithLifecycle().value

    val sendConnectionRequest: (String) -> Unit = {
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

    CallScreenContent(
        navigateAndPopUp = navigateAndPopUp,
        restartFirebaseService = restartFirebaseService,
        callStatus = callUiState,
        calleeId = calleeId,
        call = call,
        sendConnectionRequest = sendConnectionRequest,
        answerCall = answerCall,
        sendEndCall = sendEndCall,
        shouldBeMuted = shouldBeMuted
    )
}

@Composable
fun CallScreenContent(
    navigateAndPopUp: (NavigationParams) -> Unit,
    restartFirebaseService: () -> Unit,
    callStatus: CallStatus,
    calleeId: String,
    call: Call,
    sendConnectionRequest: (String) -> Unit,
    answerCall: () -> Unit,
    sendEndCall: () -> Unit,
    shouldBeMuted: (Boolean) -> Unit
) {
    when (callStatus) {
        CallStatus.STARTING -> when (call) {
            // TODO : Unnecessary now?
            is Call.CallData -> {
                if (call.callStatus != CallStatus.CALL_IN_PROGRESS && !call.isIncoming) {
                    sendConnectionRequest.invoke(call.calleeId)
                }
            }

            else -> {
                if (calleeId != CALLEE_DEFAULT_ID) {
                    sendConnectionRequest.invoke(calleeId)
                }
            }
        }

        CallStatus.CALL_FINISHED -> {
            // If there is no call invoke finish after a small delay
            LaunchedEffect(Unit) {
                //restartFirebaseService.invoke()
                delay(1500)
                navigateToContactList(navigateAndPopUp)
            }
            // Show call ended when there is no active call
            NoCallScreen()
        }

        else -> {
            TelecomCallScreen(
                callStatus = callStatus,
                call = call,
                onCallAction = {
                    when (it) {
                        is CallAction.Answer -> {
                            answerCall.invoke()
                        }

                        is CallAction.ToggleMute -> {
                            shouldBeMuted.invoke(it.isMuted)
                        }

                        is CallAction.Disconnect -> {
                            sendEndCall.invoke()
                        }
                    }
                }
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
private fun NoCallScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = "Call ended", style = MaterialTheme.typography.titleLarge)
    }
}

@Composable
private fun CallDismissedFromReceiver() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = "Call ended", style = MaterialTheme.typography.titleLarge)
            Text(text = "REALLY BIG ERROR!")
        }
    }
}

@PreviewLightDark
@Composable
fun CallScreenPreview() {
    VoiceCallTranslatorPreview {
        CallScreenContent(
            navigateAndPopUp = {},
            restartFirebaseService = {},
            callStatus = CallStatus.CALL_IN_PROGRESS,
            calleeId = CALLEE_DEFAULT_ID,
            call = Call.CallNoData,
            sendConnectionRequest = {},
            answerCall = {},
            sendEndCall = {},
            shouldBeMuted = {}
        )
    }
}