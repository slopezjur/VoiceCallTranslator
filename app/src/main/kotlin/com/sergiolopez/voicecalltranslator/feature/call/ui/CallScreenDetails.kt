package com.sergiolopez.voicecalltranslator.feature.call.ui

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.sergiolopez.voicecalltranslator.R
import com.sergiolopez.voicecalltranslator.feature.call.domain.model.Call
import com.sergiolopez.voicecalltranslator.feature.call.domain.model.CallAction
import com.sergiolopez.voicecalltranslator.feature.call.domain.model.CallStatus
import com.sergiolopez.voicecalltranslator.feature.call.domain.model.Message
import com.sergiolopez.voicecalltranslator.feature.common.domain.VctGlobalName.VCT_LOGS
import com.sergiolopez.voicecalltranslator.feature.common.utils.Dummy
import com.sergiolopez.voicecalltranslator.theme.VoiceCallTranslatorPreview

@Composable
internal fun CallScreenDetails(
    modifier: Modifier,
    callUiState: CallStatus,
    call: Call,
    onCallAction: (CallAction) -> Unit,
    messages: List<Message>
) {
    if (call is Call.CallData) {
        var isMuted by remember { mutableStateOf(false) }
        var isSpeaker by remember { mutableStateOf(false) }

        CallScreenDetailsContent(
            modifier = modifier,
            name = if (call.isIncoming) {
                call.callerEmail
            } else {
                call.calleeEmail
            },
            incoming = call.isIncoming,
            callUiState = callUiState,
            isMuted = isMuted,
            isSpeaker = isSpeaker,
            onCallAction = { callAction ->
                when (callAction) {
                    is CallAction.Answer -> {
                        onCallAction.invoke(CallAction.Answer)
                    }

                    is CallAction.ToggleMute -> {
                        isMuted = callAction.isMuted
                        onCallAction.invoke(CallAction.ToggleMute(isMuted))
                    }

                    is CallAction.ToggleSpeaker -> {
                        isSpeaker = callAction.isSpeaker
                        onCallAction.invoke(CallAction.ToggleSpeaker(isSpeaker))
                    }

                    is CallAction.Disconnect -> {
                        onCallAction.invoke(CallAction.Disconnect)
                    }
                }
            },
            messages = messages
        )
    } else {
        // If we are here...
        //onCallStatus.invoke(CallViewModel.CallUiState.ERROR)
        Log.d("$VCT_LOGS: CallScreenDetails", "$callUiState $call")
    }
}

@Composable
private fun CallScreenDetailsContent(
    modifier: Modifier,
    name: String,
    incoming: Boolean,
    callUiState: CallStatus,
    isMuted: Boolean,
    isSpeaker: Boolean,
    onCallAction: (CallAction) -> Unit,
    messages: List<Message>
) {
    Scaffold(
        topBar = {
            Column(
                modifier = modifier,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                CallInfoCard(
                    modifier = modifier,
                    name = name,
                    isActive = callUiState == CallStatus.CALL_IN_PROGRESS
                )
                HorizontalDivider()
            }
        },
        bottomBar = {
            if (incoming && (callUiState == CallStatus.STARTING || callUiState == CallStatus.INCOMING_CALL)) {
                CallIncomingActions(
                    modifier = modifier,
                    onCallAction = onCallAction
                )
            } else {
                OngoingCallActions(
                    modifier = modifier,
                    isMuted = isMuted,
                    isSpeaker = isSpeaker,
                    onCallAction = onCallAction,
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = modifier.padding(paddingValues)
        ) {
            CallScreenConversation(
                modifier = modifier,
                messages = discardRepeatedMessages(messages = messages)
            )
        }
    }
}

fun discardRepeatedMessages(messages: List<Message>): List<Message> {
    // TODO : There is a bug happening from time to time, It is duplicating messages... INVESTIGATE!
    return messages.groupBy { message ->
        message.text
    }.entries.map { messageMap ->
        messageMap.value.maxBy { messageFromMap ->
            messageFromMap.text
        }
    }
}

@Composable
private fun CallInfoCard(
    modifier: Modifier,
    name: String,
    isActive: Boolean
) {
    Column(
        modifier
            .fillMaxWidth()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            imageVector = Icons.Rounded.Person,
            contentDescription = null,
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
        )
        Text(text = name, style = MaterialTheme.typography.titleMedium)

        if (!isActive) {
            Text(
                text = stringResource(id = R.string.connecting),
                style = MaterialTheme.typography.titleSmall
            )
        } else {
            Text(
                text = stringResource(id = R.string.connected),
                style = MaterialTheme.typography.titleSmall
            )
        }
    }
}

@Composable
private fun OngoingCallActions(
    modifier: Modifier,
    isMuted: Boolean,
    isSpeaker: Boolean,
    onCallAction: (CallAction) -> Unit
) {
    Column(
        modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .shadow(1.dp)
            .padding(26.dp)
    ) {
        CallControls(
            modifier = modifier,
            isMuted = isMuted,
            isSpeaker = isSpeaker,
            onCallAction = onCallAction,
        )
    }
}

@PreviewLightDark
@Composable
fun CallScreenDetailsPreview() {
    VoiceCallTranslatorPreview {
        CallScreenDetails(
            modifier = Modifier,
            callUiState = CallStatus.STARTING,
            call = Call.CallNoData,
            onCallAction = {},
            messages = emptyList()
        )
    }
}

@PreviewLightDark
@Composable
fun CallScreenDetailsIncomingCallStartingPreview() {
    VoiceCallTranslatorPreview {
        CallScreenDetails(
            modifier = Modifier,
            callUiState = CallStatus.STARTING,
            call = Call.CallData(
                callerId = "123456",
                callerEmail = "slopezjur@uoc.edu",
                calleeId = "",
                calleeEmail = "test@test.com",
                isIncoming = true,
                callStatus = CallStatus.INCOMING_CALL,
                offerData = "offer",
                language = "es",
                timestamp = 1716836446515
            ),
            onCallAction = {},
            messages = emptyList()
        )
    }
}

@PreviewLightDark
@Composable
fun CallScreenDetailsIncomingCallInProgressPreview() {
    VoiceCallTranslatorPreview {
        CallScreenDetails(
            modifier = Modifier,
            callUiState = CallStatus.CALL_IN_PROGRESS,
            call = Call.CallData(
                callerId = "123456",
                callerEmail = "slopezjur@uoc.edu",
                calleeId = "",
                calleeEmail = "test@test.com",
                isIncoming = true,
                callStatus = CallStatus.INCOMING_CALL,
                offerData = "offer",
                language = "es",
                timestamp = 1716836446515
            ),
            onCallAction = {},
            messages = Dummy.messages
        )
    }
}

@PreviewLightDark
@Composable
fun CallScreenDetailsStartingPreview() {
    VoiceCallTranslatorPreview {
        CallScreenDetails(
            modifier = Modifier,
            callUiState = CallStatus.STARTING,
            call = Call.CallData(
                callerId = "123456",
                callerEmail = "slopezjur@uoc.edu",
                calleeId = "",
                calleeEmail = "test@test.com",
                isIncoming = false,
                callStatus = CallStatus.STARTING,
                offerData = "offer",
                language = "es",
                timestamp = 1716836446515
            ),
            onCallAction = {},
            messages = emptyList()
        )
    }
}

@PreviewLightDark
@Composable
fun CallScreenDetailsCallInProgressPreview() {
    VoiceCallTranslatorPreview {
        CallScreenDetails(
            modifier = Modifier,
            callUiState = CallStatus.CALL_IN_PROGRESS,
            call = Call.CallData(
                callerId = "123456",
                callerEmail = "slopezjur@uoc.edu",
                calleeId = "",
                calleeEmail = "test@test.com",
                isIncoming = false,
                callStatus = CallStatus.CALL_IN_PROGRESS,
                offerData = "offer",
                language = "es",
                timestamp = 1716836446515
            ),
            onCallAction = {},
            messages = Dummy.messages
        )
    }
}

@PreviewLightDark
@Composable
fun CallScreenDetailsCallInProgressOneMessagesPreview() {
    VoiceCallTranslatorPreview {
        CallScreenDetails(
            modifier = Modifier,
            callUiState = CallStatus.CALL_IN_PROGRESS,
            call = Call.CallData(
                callerId = "123456",
                callerEmail = "slopezjur@uoc.edu",
                calleeId = "",
                calleeEmail = "test@test.com",
                isIncoming = true,
                callStatus = CallStatus.INCOMING_CALL,
                offerData = "offer",
                language = "es",
                timestamp = 1716836446515
            ),
            onCallAction = {},
            messages = listOf(Dummy.message)
        )
    }
}

@PreviewLightDark
@Composable
fun CallScreenDetailsReconnectingPreview() {
    VoiceCallTranslatorPreview {
        CallScreenDetails(
            modifier = Modifier,
            callUiState = CallStatus.RECONNECTING,
            call = Call.CallData(
                callerId = "123456",
                callerEmail = "slopezjur@uoc.edu",
                calleeId = "",
                calleeEmail = "test@test.com",
                isIncoming = false,
                callStatus = CallStatus.CALL_IN_PROGRESS,
                offerData = "offer",
                language = "es",
                timestamp = 1716836446515
            ),
            onCallAction = {},
            messages = Dummy.messages
        )
    }
}