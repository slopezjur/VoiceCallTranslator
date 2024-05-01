package com.sergiolopez.voicecalltranslator.feature.call.ui

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
import com.sergiolopez.voicecalltranslator.feature.call.domain.model.Call
import com.sergiolopez.voicecalltranslator.feature.call.domain.model.CallStatus
import com.sergiolopez.voicecalltranslator.feature.call.telecom.model.TelecomCall
import com.sergiolopez.voicecalltranslator.navigation.NavigationParams
import com.sergiolopez.voicecalltranslator.navigation.NavigationRoute
import com.sergiolopez.voicecalltranslator.theme.VoiceCallTranslatorPreview
import java.time.Instant

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
            //callViewModel.startCall(calleeId)
            if (calleeId != "-1") {
                callViewModel.sendConnectionRequest(
                    calleeId = calleeId
                )
                context.startNewCall(
                    call = Call.CallData(
                        callerId = "",
                        calleeId = calleeId,
                        offerData = null,
                        answerData = null,
                        isIncoming = false,
                        callStatus = CallStatus.CALLING,
                        timestamp = Instant.now().epochSecond
                    )
                    /*DataModel(
                    sender = calleeId,
                    type = DataModelType.Offer
                )*/
                )
            }
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
                        onCallFinished = {
                            callViewModel.sendEndCall(calleeId)
                            openAndPopUp.invoke(
                                NavigationParams(
                                    NavigationRoute.CONTACT_LIST.navigationName,
                                    NavigationRoute.CALL.navigationName
                                )
                            )
                        }
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