package com.sergiolopez.voicecalltranslator.feature.call.ui

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.hilt.navigation.compose.hiltViewModel
import com.sergiolopez.voicecalltranslator.feature.call.domain.TelecomCallManager.Companion.startNewCall
import com.sergiolopez.voicecalltranslator.feature.call.telecom.model.TelecomCall
import com.sergiolopez.voicecalltranslator.navigation.NavigationParams
import com.sergiolopez.voicecalltranslator.theme.VoiceCallTranslatorPreview

@Composable
fun CallScreen(
    openAndPopUp: (NavigationParams) -> Unit,
    calleeId: String,
    callViewModel: CallViewModel = hiltViewModel()
) {
    val callUiState = callViewModel.callUiState.collectAsState().value

    val context = LocalContext.current

    val telecomCall by callViewModel.telecomCallState.collectAsState()

    LaunchedEffect(Unit) {
        callViewModel.subscribeTelecomCallState()
    }

    val hasOngoingCall = telecomCall is TelecomCall.Registered

    when (callUiState) {
        CallViewModel.CallUiState.STARTING -> {
            callViewModel.startCall(calleeId)
            context.startNewCall(
                name = "Bob",
                uri = Uri.parse(calleeId),
            )
            callViewModel.setCallUiState(CallViewModel.CallUiState.CALLING)
        }

        else -> {
            when {
                (isCallingAndHasOngoingCall(
                    callUiState = callUiState,
                    hasOngoingCall = hasOngoingCall
                )) || isCallInProgress(
                    callUiState = callUiState
                ) -> {
                    TelecomCallScreen(
                        telecomCall = telecomCall,
                        onCallFinished = {}
                    )
                    callViewModel.setCallUiState(CallViewModel.CallUiState.CALL_IN_PROGRESS)
                }

                else -> {
                    Column(
                        Modifier
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}

@Composable
private fun isCallingAndHasOngoingCall(
    callUiState: CallViewModel.CallUiState,
    hasOngoingCall: Boolean
) = callUiState == CallViewModel.CallUiState.CALLING && hasOngoingCall

@Composable
private fun isCallInProgress(callUiState: CallViewModel.CallUiState) =
    callUiState == CallViewModel.CallUiState.CALL_IN_PROGRESS

@Composable
fun CallScreenContent(
    openAndPopUp: (NavigationParams) -> Unit,
    calleeId: String,
    callUiState: CallViewModel.CallUiState
) {
    Text(text = calleeId)
}

@PreviewLightDark
@Composable
fun CallScreenPreview() {
    VoiceCallTranslatorPreview {
        CallScreenContent(
            openAndPopUp = {},
            calleeId = "01",
            callUiState = CallViewModel.CallUiState.CALL_IN_PROGRESS
        )
    }
}