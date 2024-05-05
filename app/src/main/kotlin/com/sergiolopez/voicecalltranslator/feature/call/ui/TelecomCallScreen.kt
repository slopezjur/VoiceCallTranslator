package com.sergiolopez.voicecalltranslator.feature.call.ui

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.MicOff
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.unit.dp
import com.sergiolopez.voicecalltranslator.feature.call.domain.model.Call
import com.sergiolopez.voicecalltranslator.feature.call.domain.model.CallAction
import com.sergiolopez.voicecalltranslator.feature.call.domain.model.CallStatus

@Composable
internal fun TelecomCallScreen(
    callStatus: CallStatus,
    call: Call,
    onCallAction: (CallAction) -> Unit
) {
    if (call is Call.CallData) {
        var isMuted by remember { mutableStateOf(false) }

        TelecomCallScreenContent(
            name = call.callerId,
            info = call.offerData,
            incoming = call.isIncoming,
            isActive = callStatus == CallStatus.CALL_IN_PROGRESS,
            isMuted = isMuted,
            onCallAction = { callAction ->
                when (callAction) {
                    is CallAction.Answer -> {
                        onCallAction.invoke(CallAction.Answer)
                    }

                    is CallAction.ToggleMute -> {
                        isMuted = callAction.isMuted
                        onCallAction.invoke(CallAction.ToggleMute(isMuted))
                    }

                    is CallAction.Disconnect -> {
                        onCallAction.invoke(CallAction.Disconnect)
                    }
                }
            }
        )
    } else {
        // IF we are here...
        //onCallStatus.invoke(CallViewModel.CallUiState.ERROR)
        Log.d("VCT_LOGS: TelecomCallScreen", "$callStatus $call")
    }
}

@Composable
private fun TelecomCallScreenContent(
    name: String,
    info: String,
    incoming: Boolean,
    isActive: Boolean,
    isMuted: Boolean,
    onCallAction: (CallAction) -> Unit,
) {
    Column(
        Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CallInfoCard(name, info, isActive)
        if (incoming && !isActive) {
            IncomingCallActions(onCallAction)
        } else {
            OngoingCallActions(
                isMuted = isMuted,
                onCallAction = onCallAction,
            )
        }
    }
}

@Composable
private fun OngoingCallActions(
    isMuted: Boolean,
    onCallAction: (CallAction) -> Unit,
) {
    Column(
        Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .shadow(1.dp)
            .padding(26.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CallControls(
            isMuted = isMuted,
            onCallAction = onCallAction,
        )
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
                modifier = Modifier.rotate(90f),
            )
        }
    }
}

@Composable
private fun IncomingCallActions(onCallAction: (CallAction) -> Unit) {
    Row(
        Modifier
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
                modifier = Modifier.rotate(90f),
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
private fun CallInfoCard(name: String, info: String, isActive: Boolean) {
    Column(
        Modifier
            .fillMaxSize(0.5f),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(imageVector = Icons.Rounded.Person, contentDescription = null)
        Text(text = name, style = MaterialTheme.typography.titleMedium)
        Text(text = info, style = MaterialTheme.typography.bodyMedium)

        if (!isActive) {
            Text(text = "Connecting...", style = MaterialTheme.typography.titleSmall)
        } else {
            Text(text = "Connected", style = MaterialTheme.typography.titleSmall)
        }

    }
}

@Composable
private fun CallControls(
    isMuted: Boolean,
    onCallAction: (CallAction) -> Unit,
) {
    Row(
        modifier = Modifier
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
    }
}
