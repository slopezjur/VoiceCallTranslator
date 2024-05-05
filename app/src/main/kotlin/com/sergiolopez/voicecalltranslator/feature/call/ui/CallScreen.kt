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
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sergiolopez.voicecalltranslator.feature.call.domain.model.Call
import com.sergiolopez.voicecalltranslator.feature.call.domain.model.CallStatus
import com.sergiolopez.voicecalltranslator.navigation.CALLEE_DEFAULT_ID
import com.sergiolopez.voicecalltranslator.navigation.NavigationCallExtra
import com.sergiolopez.voicecalltranslator.navigation.NavigationParams
import com.sergiolopez.voicecalltranslator.navigation.NavigationRoute
import com.sergiolopez.voicecalltranslator.theme.VoiceCallTranslatorPreview
import kotlinx.coroutines.delay

@Composable
fun CallScreen(
    openAndPopUp: (NavigationParams) -> Unit,
    calleeId: String,
    navigationCallExtra: NavigationCallExtra,
    restartFirebaseService: () -> Unit,
    callViewModel: CallViewModel = hiltViewModel()
) {
    val callUiState = callViewModel.callUiState.collectAsState().value
    val call = callViewModel.callState.collectAsState().value

    if (callUiState == CallViewModel.CallUiState.STARTING && navigationCallExtra.hasCallData && calleeId == CALLEE_DEFAULT_ID) {
        val callData = navigationCallExtra.call as Call.CallData
        when (callData.callStatus) {
            CallStatus.INCOMING_CALL -> {
                callViewModel.setCallUiState(CallViewModel.CallUiState.INCOMING_CALL)
            }

            CallStatus.CALL_IN_PROGRESS -> {
                callViewModel.setCallUiState(CallViewModel.CallUiState.CALL_IN_PROGRESS)
            }

            else -> {
                // All good?
                Unit
            }
        }
    }

    val sendConnectionRequest: (String) -> Unit = {
        callViewModel.sendConnectionRequest(it)
    }

    val answerCall: () -> Unit = {
        callViewModel.answerCall()
    }

    val sendEndCall: () -> Unit = {
        callViewModel.sendEndCall()
    }

    val setCallUiState: (CallViewModel.CallUiState) -> Unit = {
        callViewModel.setCallUiState(it)
    }

    CallScreenContent(
        openAndPopUp = openAndPopUp,
        restartFirebaseService = restartFirebaseService,
        callUiState = callUiState,
        calleeId = calleeId,
        call = call,
        sendConnectionRequest = sendConnectionRequest,
        answerCall = answerCall,
        sendEndCall = sendEndCall,
        setCallUiState = setCallUiState
    )
}

@Composable
fun CallScreenContent(
    openAndPopUp: (NavigationParams) -> Unit,
    restartFirebaseService: () -> Unit,
    callUiState: CallViewModel.CallUiState,
    calleeId: String,
    call: Call,
    sendConnectionRequest: (String) -> Unit,
    answerCall: () -> Unit,
    sendEndCall: () -> Unit,
    setCallUiState: (CallViewModel.CallUiState) -> Unit,
) {
    when (callUiState) {
        CallViewModel.CallUiState.STARTING -> when (call) {
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

        CallViewModel.CallUiState.ANSWERING -> {
            answerCall.invoke()
        }

        CallViewModel.CallUiState.ERROR -> {
            CallDismissedFromReceiver()
            LaunchedEffect(Unit) {
                delay(1000)
                setCallUiState.invoke(CallViewModel.CallUiState.CALL_FINISHED)
            }
        }

        CallViewModel.CallUiState.FINISHING_CALL -> {
            // TODO : Reset from ViewModel?
            sendEndCall()
            // If there is no call invoke finish after a small delay
            LaunchedEffect(Unit) {
                delay(3000)
            }
            // Show call ended when there is no active call
            NoCallScreen()
        }

        CallViewModel.CallUiState.CALL_FINISHED -> {
            restartFirebaseService.invoke()
            navigateToContactList(openAndPopUp)
        }

        else -> {
            TelecomCallScreen(
                callUiState = callUiState,
                call = call,
                onCallStatus = {
                    setCallUiState(it)
                }
            )

            /*Column(
                Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                CircularProgressIndicator()
            }*/
        }
    }
}

private fun navigateToContactList(openAndPopUp: (NavigationParams) -> Unit) {
    openAndPopUp.invoke(
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
            openAndPopUp = {},
            restartFirebaseService = {},
            callUiState = CallViewModel.CallUiState.CALL_IN_PROGRESS,
            calleeId = CALLEE_DEFAULT_ID,
            call = Call.CallNoData,
            sendConnectionRequest = {},
            answerCall = {},
            sendEndCall = {},
            setCallUiState = {},
        )
    }
}