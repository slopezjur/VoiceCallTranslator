package com.sergiolopez.voicecalltranslator.feature.call.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.hilt.navigation.compose.hiltViewModel
import com.sergiolopez.voicecalltranslator.feature.call.telecom.model.TelecomCall
import com.sergiolopez.voicecalltranslator.feature.call.telecom.repository.TelecomCallRepository
import com.sergiolopez.voicecalltranslator.feature.call.telecom.service.TelecomCallService
import com.sergiolopez.voicecalltranslator.feature.call.telecom.service.TelecomCallService.Companion.ACTION_OUTGOING_CALL
import com.sergiolopez.voicecalltranslator.navigation.NavigationParams
import com.sergiolopez.voicecalltranslator.theme.VoiceCallTranslatorPreview

@Composable
fun CallScreen(
    openAndPopUp: (NavigationParams) -> Unit,
    callViewModel: CallViewModel = hiltViewModel(),
    calleeId: String,
    telecomCallRepository: TelecomCallRepository
) {
    val callUiState = callViewModel.callUiState.collectAsState().value

    //callViewModel.startCall(calleeId)

    val context = LocalContext.current
    val telecomCallRepositoryRemember = remember {
        TelecomCallRepository.instance ?: TelecomCallRepository.create(context.applicationContext)
    }

    val call by telecomCallRepositoryRemember.currentCall.collectAsState()
    val hasOngoingCall = call is TelecomCall.Registered

    when (callUiState) {
        CallViewModel.CallUiState.STARTING -> {
            context.launchCall(
                action = ACTION_OUTGOING_CALL,
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
                        telecomCallRepository = telecomCallRepositoryRemember,
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

private fun Context.launchCall(action: String, name: String, uri: Uri) {
    startService(
        Intent(this, TelecomCallService::class.java).apply {
            this.action = action
            putExtra(TelecomCallService.EXTRA_NAME, name)
            putExtra(TelecomCallService.EXTRA_URI, uri)
        },
    )
}

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