package com.sergiolopez.voicecalltranslator.feature.call.ui

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.MicOff
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.SpatialAudioOff
import androidx.compose.material.icons.rounded.SpeakerPhone
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
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
    callStatus: CallStatus,
    call: Call,
    onCallAction: (CallAction) -> Unit,
    messages: List<Message>
) {
    if (call is Call.CallData) {
        var isMuted by remember { mutableStateOf(false) }
        var isSpeaker by remember { mutableStateOf(false) }

        CallScreenDetailsContent(
            modifier = modifier,
            name = call.callerId,
            info = call.offerData,
            incoming = call.isIncoming,
            isActive = callStatus == CallStatus.CALL_IN_PROGRESS,
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
        // IF we are here...
        //onCallStatus.invoke(CallViewModel.CallUiState.ERROR)
        Log.d("$VCT_LOGS: TelecomCallScreen", "$callStatus $call")
    }
}

@Composable
private fun CallScreenDetailsContent(
    modifier: Modifier,
    name: String,
    info: String,
    incoming: Boolean,
    isActive: Boolean,
    isMuted: Boolean,
    isSpeaker: Boolean,
    onCallAction: (CallAction) -> Unit,
    messages: List<Message>
) {
    Box(
        modifier
            .fillMaxSize(),
    ) {
        Column(
            modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            CallInfoCard(
                modifier = modifier,
                name = name,
                info = info,
                isActive = isActive
            )
            HorizontalDivider()
            Spacer(modifier = Modifier.weight(1f))
            CallScreenConversation(
                modifier = modifier,
                messages = messages
            )
            if (incoming && !isActive) {
                IncomingCallActions(
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
            .padding(26.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CallControls(
            modifier = modifier,
            isMuted = isMuted,
            isSpeaker = isSpeaker,
            onCallAction = onCallAction,
        )
    }
}

@Composable
private fun IncomingCallActions(
    modifier: Modifier,
    onCallAction: (CallAction) -> Unit
) {
    Row(
        modifier
            .fillMaxWidth()
            .padding(26.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        FloatingActionButton(
            onClick = {
                onCallAction(
                    CallAction.Disconnect
                )
            },
            containerColor = MaterialTheme.colorScheme.error,
        ) {
            Icon(
                imageVector = Icons.Rounded.Call,
                contentDescription = null,
                modifier = modifier.rotate(90f),
            )
        }
        FloatingActionButton(
            onClick = {
                onCallAction(
                    CallAction.Answer
                )
            },
            containerColor = MaterialTheme.colorScheme.primary,
        ) {
            Icon(imageVector = Icons.Rounded.Call, contentDescription = null)
        }
    }
}

@Composable
private fun CallInfoCard(
    modifier: Modifier,
    name: String,
    info: String,
    isActive: Boolean
) {
    Column(
        modifier.padding(48.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            imageVector = Icons.Rounded.Person,
            contentDescription = null,
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
        )
        Text(text = name, style = MaterialTheme.typography.titleMedium)
        Text(text = info, style = MaterialTheme.typography.bodyMedium)

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
private fun CallControls(
    modifier: Modifier,
    isMuted: Boolean,
    isSpeaker: Boolean,
    onCallAction: (CallAction) -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        IconToggleButton(
            checked = isMuted,
            onCheckedChange = {
                onCallAction(
                    CallAction.ToggleMute(
                        isMuted = it
                    )
                )
            },
        ) {
            if (isMuted) {
                Icon(imageVector = Icons.Rounded.MicOff, contentDescription = "Mic on")
            } else {
                Icon(imageVector = Icons.Rounded.Mic, contentDescription = "Mic off")
            }
        }
        IconToggleButton(
            checked = isSpeaker,
            onCheckedChange = {
                onCallAction(
                    CallAction.ToggleSpeaker(
                        isSpeaker = it
                    )
                )
            },
        ) {
            if (isSpeaker) {
                Icon(imageVector = Icons.Rounded.SpeakerPhone, contentDescription = "Speaker on")
            } else {
                Icon(
                    imageVector = Icons.Rounded.SpatialAudioOff,
                    contentDescription = "Speaker off"
                )
            }
        }

        FloatingActionButton(
            onClick = {
                onCallAction(
                    CallAction.Disconnect,
                )
            },
            containerColor = MaterialTheme.colorScheme.error,
        ) {
            Icon(
                imageVector = Icons.Rounded.Call,
                contentDescription = "Disconnect call",
                modifier = modifier.rotate(90f),
            )
        }
    }
}

@PreviewLightDark
@Composable
fun CallScreenDetailsPreview() {
    VoiceCallTranslatorPreview {
        CallScreenDetails(
            modifier = Modifier,
            callStatus = CallStatus.STARTING,
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
            callStatus = CallStatus.STARTING,
            call = Call.CallData(
                callerId = "slopezjur@uoc.edu",
                calleeId = "",
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
            callStatus = CallStatus.CALL_IN_PROGRESS,
            call = Call.CallData(
                callerId = "slopezjur@uoc.edu",
                calleeId = "",
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
            callStatus = CallStatus.STARTING,
            call = Call.CallData(
                callerId = "slopezjur@uoc.edu",
                calleeId = "",
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
            callStatus = CallStatus.CALL_IN_PROGRESS,
            call = Call.CallData(
                callerId = "slopezjur@uoc.edu",
                calleeId = "",
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